package org.back.devsnackshop_back.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.middlewareManage.MiddlewareRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AnsibleExecutor {
    private final ObjectMapper mapper = new ObjectMapper();

    @Async  // 미들웨어 설치 비동기 설정
    public CompletableFuture<Boolean> installWithVersions(String hostIp, List<MiddlewareRequest> requests) {
        log.info("설치 프로세스 시작 - 대상 IP: {}, 항목 수: {}", hostIp, requests.size());

        try {

            String jsonItems = mapper.writeValueAsString(requests);

            String extraVars = String.format("{\"install_items\": %s}", jsonItems);

            List<String> command = List.of(
                    "ansible-playbook",
                    "-i", hostIp + ",",
                    "src/main/resources/ansible/install_with_versions.yml",
                    "--extra-vars", extraVars
            );
            log.info("실행 명령어: {}", String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[Ansible Log] {}", line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("미들웨어 설치 성공 (IP: {}", hostIp);
                return CompletableFuture.completedFuture(true);
            } else {
                log.error("미들웨어 설치 실패 - 종료 코드: {} IP: {}", exitCode, hostIp);
                return CompletableFuture.completedFuture(false);
            }
        }
        catch (JsonProcessingException jsonExcep) {
            jsonExcep.printStackTrace();
            log.error("JSON 파싱 오류", jsonExcep);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("명령어 실행 중 입출력 오류 발생", ioe);
        }
        catch (InterruptedException ire) {
            ire.printStackTrace();
            log.error("프로세스 중단", ire);
        }
        return CompletableFuture.completedFuture(false);
    }
}
