package org.back.devsnackshop_back.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.middlewareManage.InstallRequest;
import org.back.devsnackshop_back.entity.UserOsInstanceEntity;
import org.back.devsnackshop_back.repository.UserOsInstanceRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class MiddlewareService {
    private final UserOsInstanceRepository userOsInstanceRepository;
    private final Map<String, String> statusStore = new ConcurrentHashMap<>();

    @Async("installExecutor")
    public void installMiddlewaresAsync(String taskId, InstallRequest dto) {
        statusStore.put(taskId, "RUNNING");

        JSch jsch = new JSch();
        Session session = null;

        try {
            if (dto.getUserOsInstanceId() != null && dto.getUserOsInstanceId() != 0) {
                dto = setRequestDto(dto);
            }

            if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
                handleSshAuthentication(jsch, dto);
            }

            session = jsch.getSession(dto.getUsername(), dto.getIp(), Math.toIntExact(dto.getPort()));

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                session.setPassword(dto.getPassword());
            }

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(15000);

            if (dto.getInstallPath() != null && !dto.getInstallPath().isEmpty()) {
                String checkCmd;
                if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                    checkCmd = String.format("echo '%s' | sudo -S [ -w \"%s\" ] && echo \"OK\" || echo \"NO_AUTH\"",
                            dto.getPassword(), dto.getInstallPath());
                } else {
                    checkCmd = String.format("sudo [ -w \"%s\" ] && echo \"OK\" || echo \"NO_AUTH\"", dto.getInstallPath());
                }

                String authResult = executeCommand(session, checkCmd).trim();
                if (authResult.contains("NO_AUTH")) {
                    statusStore.put(taskId, "FAILED: 해당 경로에 접근 권한이 없습니다.");
                    return;
                }
            }

            String fullScript = buildFullScript(dto);
            log.info("[{}] 설치 시작: {}", taskId, fullScript);

            executeCommand(session, fullScript);

            statusStore.put(taskId, "SUCCESS");
            log.info("[{}] 모든 미들웨어 설치 완료", taskId);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("[{}] 설치 실패: {}", taskId, e.getMessage());
        }
        finally {
            if (session != null) session.disconnect();
        }
    }

    // 상태 확인용 메소드
    public String getStatus(String taskId) {
        return statusStore.getOrDefault(taskId, "NOT_FOUND");
    }

    private String buildFullScript(InstallRequest dto) {
        StringBuilder sb = new StringBuilder();
        String os = dto.getOs().toLowerCase();

        // 패스워드 주입이 필요한 경우 sudo 명령어 앞에 echo 추가
        String sudoPrefix = (dto.getPassword() != null && !dto.getPassword().isEmpty())
                ? String.format("echo '%s' | sudo -S ", dto.getPassword()) : "sudo ";

        sb.append(sudoPrefix).append(getUpdateCommand(os)).append(" && ");

        for (int i = 0; i < dto.getMiddlewares().size(); i++) {
            String mw = dto.getMiddlewares().get(i);
            String ver = (dto.getMiddlewares().size() > 1) ? null : dto.getMwVersion();
            sb.append(sudoPrefix).append(buildSingleInstallCmd(os, mw, ver));
            if (i < dto.getMiddlewares().size() - 1) sb.append(" && ");
        }
        return sb.toString();
    }

    private String getUpdateCommand(String os) {
        return (os.contains("ubuntu") || os.contains("debian")) ? "sudo apt-get update -y" : "sudo yum makecache";
    }

    private String buildSingleInstallCmd(String os, String mw, String version) {
        String pkgMgr = (os.contains("ubuntu") || os.contains("debian")) ? "apt-get" : "yum";
        String verSep = pkgMgr.equals("apt-get") ? "=" : "-";
        if (version == null || version.isEmpty()) {
            return String.format("sudo %s install -y %s", pkgMgr, mw);
        } else {
            return String.format("sudo %s install -y %s%s%s", pkgMgr, mw, verSep, version);
        }
    }

    private String executeCommand(Session session, String command) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        InputStream in = channel.getInputStream();
        channel.connect();

        StringBuilder output = new StringBuilder();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                output.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) break;
            Thread.sleep(100);
        }
        channel.disconnect();
        return output.toString();
    }

    private void handleSshAuthentication(JSch jsch, InstallRequest dto) throws Exception {
        if (dto.getKeyFile() != null && !dto.getKeyFile().isEmpty()) {
            jsch.addIdentity("uploaded-key", dto.getKeyFile().getBytes(), null, null);
            log.info("업로드된 인증키 파일을 사용합니다.");
        }
        else if (dto.getUserOsInstanceId() != 0) {
            UserOsInstanceEntity entity = userOsInstanceRepository.findById(dto.getUserOsInstanceId())
                    .orElseThrow(() -> new RuntimeException("서버 정보를 찾을 수 없습니다."));

            String keyPath = "/home/ubuntu/keyFiles/" + entity.getAuthKeyFilename();
            if (keyPath != null && !keyPath.isEmpty()) {
                jsch.addIdentity(keyPath);
                log.info("서버에 저장된 인증키를 사용합니다: {}", keyPath);
            } else {
                throw new RuntimeException("인증 정보(Password 또는 KeyFile)가 없습니다.");
            }
        }
    }

    private InstallRequest setRequestDto(InstallRequest dto) {
        UserOsInstanceEntity entity = userOsInstanceRepository.findById(dto.getUserOsInstanceId()).orElseThrow();
        dto.setIp(entity.getIpAddress());
        dto.setUsername(entity.getUsername());
        dto.setPort(entity.getPortNumber());
        dto.setOs(entity.getOsId().getDistroName());
        dto.setPassword(entity.getPassword());

        return dto;
    }
}
