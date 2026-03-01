package org.back.devsnackshop_back.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.middlewareManage.InstallRequest;
import org.back.devsnackshop_back.entity.AttachmentEntity;
import org.back.devsnackshop_back.entity.UserOsInstanceEntity;
import org.back.devsnackshop_back.repository.AttachmentRepository;
import org.back.devsnackshop_back.repository.UserOsInstanceRepository;
import org.back.devsnackshop_back.task.MiddlewareTask;
import org.back.devsnackshop_back.task.MiddlewareTaskFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstallMiddlewareService {
    private final MiddlewareService middlewareService;
    private final UserOsInstanceRepository userOsInstanceRepository;
    private final AttachmentRepository attachmentRepository;

    private final MiddlewareTaskFactory middlewareTaskFactory;

    /**
     * DB 조회만 수행하고 트랜잭션을 즉시 종료하여 커넥션을 반환한다.
     */
    @Transactional(readOnly = true)
    public SshConnectionInfo loadSshConnectionInfo(Long userOsInstanceId) {
        UserOsInstanceEntity instance = userOsInstanceRepository.findByIdWithAll(userOsInstanceId)
                .orElseThrow(() -> new IllegalArgumentException("서버 정보를 찾을 수 없습니다."));

        AttachmentEntity keyFile = (instance.getAttachmentId() != null)
                ? attachmentRepository.findById(instance.getAttachmentId()).orElse(null)
                : null;

        // 엔티티에서 필요한 값만 추출하여 반환 (Lazy 로딩 방지 + 커넥션 즉시 반환)
        return new SshConnectionInfo(
                instance.getUsername(),
                instance.getIpAddress(),
                instance.getPortNumber().intValue(),
                instance.getPassword(),
                keyFile != null ? keyFile.getFilePath() : null
        );
    }

    @Async("installExecutor")
    public void installMiddleware(InstallRequest request) {
        try {
            // 1. DB 조회 (트랜잭션 시작 → 조회 → 커밋 → 커넥션 반환)
            SshConnectionInfo connInfo = loadSshConnectionInfo(request.getUserOsInstanceId());

            // 2. 설치 명령어 생성 (DB 커넥션 불필요)
            List<String> commands = new ArrayList<>();
            String sudoPrefix = connInfo.username().equals("root") ? "" : "sudo -S -p '' ";

            for (int i = 0; i < request.getMiddlewares().size(); i++) {
                String mwName = request.getMiddlewares().get(i);
                String version = (request.getMwVersion() != null && request.getMwVersion().size() > i)
                        ? request.getMwVersion().get(i) : "";

                MiddlewareTask task = middlewareTaskFactory.getTask(mwName);

                String installPath = request.getInstallPath();

                if (installPath != null && !installPath.trim().isEmpty()) {
                    log.info("Installing {} via Binary to path: {}", mwName, installPath);
                    commands.addAll(task.getBinaryInstallCommand(installPath.trim(), version, sudoPrefix));
                } else {
                    log.info("Installing {} via Package Manager (apt)", mwName);
                    commands.addAll(task.getPackageInstallCommand(version, sudoPrefix));
                }
            }

            // 3. SSH 실행 (DB 커넥션 없이 수행)
            if (!commands.isEmpty()) {
                try {
                    sshExecutor(connInfo, commands);
                    log.info("✅ SSH 실행 완료");

                    // 4. DB 저장 (새 트랜잭션으로 커넥션 획득 → 저장 → 커밋 → 반환)
                    middlewareService.saveMiddlewareInstall(request);
                    log.info("✅ DB 저장 완료");
                } catch (Exception e) {
                    log.error("❌ SSH 실행 실패: {}", e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Middleware Install Error: {}", e.getMessage(), e);
        }
    }

    private void sshExecutor(SshConnectionInfo connInfo, List<String> commands) throws Exception {
        JSch jSch = new JSch();
        Session session = null;

        try {
            if (connInfo.keyFilePath() != null) {
                jSch.addIdentity(connInfo.keyFilePath());
            }

            session = jSch.getSession(connInfo.username(), connInfo.ipAddress(), connInfo.port());

            if (connInfo.keyFilePath() == null) {
                session.setPassword(connInfo.password());
            }

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(15000);

            for (String command : commands) {
                log.info("Executing command: {}", command);
                executeSingleCommand(session, command, connInfo.password());
            }
        } finally {
            if (session != null) session.disconnect();
        }
    }

    private void executeSingleCommand(Session session, String command, String password) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setErrStream(System.err);

        InputStream in = channel.getInputStream();
        OutputStream out = channel.getOutputStream();

        channel.connect();

        if (command.contains("sudo -S") && password != null) {
            out.write((password + "\n").getBytes());
            out.flush();
        }

        byte[] tmp = new byte[1024];
        long startTime = System.currentTimeMillis();
        long timeout = 5 * 60 * 1000L; // 5분 타임아웃

        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                log.info("[SSH Log] {}", new String(tmp, 0, i).trim());
            }

            if (channel.isClosed()) {
                if (in.available() > 0) continue;
                log.info("Command Exit Status: {}", channel.getExitStatus());
                break;
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                log.error("❌ Command timed out: {}", command);
                break;
            }

            Thread.sleep(100);
        }
        channel.disconnect();
    }

    /**
     * SSH 접속에 필요한 정보만 담는 record (엔티티 대신 사용)
     */
    public record SshConnectionInfo(
            String username,
            String ipAddress,
            int port,
            String password,
            String keyFilePath
    ) {}
}
