package org.back.devsnackshop_back.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.middlewareManage.InstallRequest;
import org.back.devsnackshop_back.dto.middlewareManage.response.MiddlewareListResponse;
import org.back.devsnackshop_back.entity.InstalledMiddlewareEntity;
import org.back.devsnackshop_back.entity.UserOsInstanceEntity;
import org.back.devsnackshop_back.repository.InstalledMiddlewareRepository;
import org.back.devsnackshop_back.repository.UserOsInstanceRepository;
import org.back.devsnackshop_back.task.MiddlewareTask;
import org.back.devsnackshop_back.task.MiddlewareTaskFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class MiddlewareService {
    private final UserOsInstanceRepository userOsInstanceRepository;
    private final InstalledMiddlewareRepository installedMiddlewareRepository;

    private final MiddlewareTaskFactory middlewareTaskFactory;

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

            String finalCmd = buildFullScript(dto);
            log.info("[{}] 최종 실행 명령어: \n{}", taskId, finalCmd);

            executeCommand(session, finalCmd);
            statusStore.put(taskId, "SUCCESS");
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

    public List<MiddlewareListResponse> middlewareList(long userOsId) {
        List<MiddlewareListResponse> result = new ArrayList<>();

        UserOsInstanceEntity userOs = userOsInstanceRepository.findById(userOsId)
                .orElseThrow(() -> new EntityNotFoundException("UserOs not found with ID: " + userOsId));

        List<InstalledMiddlewareEntity> mdList = installedMiddlewareRepository.findByUserOsId(userOs);

        for (InstalledMiddlewareEntity md : mdList) {
            MiddlewareListResponse res = MiddlewareListResponse.builder()
                    .name(md.getMiddlewareId().getMiddlewareName())
                    .type(md.getMiddlewareId().getMiddlewareType())
                    .version(md.getMiddlewareId().getVersion())
                    .path(md.getInstallPath())
                    .build();

            result.add(res);
        }

        return result;
    }

    private String getSudoPrefix(InstallRequest dto) {
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            return String.format("echo '%s' | sudo -S ", dto.getPassword());
        }
        return "sudo ";
    }

    private String buildFullScript(InstallRequest dto) {
        StringBuilder sb = new StringBuilder();
        String sudoPrefix = getSudoPrefix(dto);

        sb.append(sudoPrefix).append("apt-get update -y && ");

        for (int i = 0; i < dto.getMiddlewares().size(); i++) {
            String mwName = dto.getMiddlewares().get(i);
            MiddlewareTask task = middlewareTaskFactory.getTask(mwName);
            String version = dto.getMwVersion();

            if (dto.getInstallPath() != null && !dto.getInstallPath().isEmpty()) {
                sb.append(task.getBinaryInstallCommand(dto.getInstallPath(), version, sudoPrefix));
            } else {
                sb.append(task.getPackageInstallCommand(version, sudoPrefix));
            }

            if (i < dto.getMiddlewares().size() - 1) sb.append(" && ");
        }
            return sb.toString();
    }

    private String executeCommand(Session session, String command) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        channel.setErrStream(System.err);
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
            if (channel.isClosed()) {
                if (in.available() > 0) continue;
                log.info("exit status: " + channel.getExitStatus());
                break;
            }
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
