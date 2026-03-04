package org.back.devsnackshop_back.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.back.devsnackshop_back.dto.systemLog.*;
import org.back.devsnackshop_back.enums.LogType;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class SystemLogAnalyzer {

    // ✅ 배포판에 따라 파일 자동 선택
    public String readLog(ServerConnection conn, LogType type) {
        return switch (type) {
            case SYSLOG -> readFirst(conn, "/var/log/syslog", "/var/log/messages");
            case AUTH   -> readFirst(conn, "/var/log/auth.log", "/var/log/secure");
            case KERNEL -> readFirst(conn, "/var/log/kern.log",
                    "journalctl -k -n 500 --no-pager");
        };
    }

    private String execute(ServerConnection conn, String command) {
        JSch jsch = new JSch();
        JSch.setLogger(new com.jcraft.jsch.Logger() {
            public boolean isEnabled(int level) { return true; }
            public void log(int level, String message) { System.out.println("[JSch] " + message); }
        });

        try {
            setupAuth(jsch, conn);

            Session session = jsch.getSession(conn.getUser(), conn.getHost(), 40022);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            String authMethods = (conn.getAuthType() == ServerConnection.AuthType.PASSWORD) ? "password" : "publickey,password";
            config.put("PreferredAuthentications", authMethods);
            session.setConfig(config);

            if (conn.getAuthType() == ServerConnection.AuthType.PASSWORD) {
                session.setPassword(conn.getPassword());
            }

            try {
                session.connect(5000);
            } catch (com.jcraft.jsch.JSchException e) {
                if (e.getMessage().contains("Auth fail") && conn.getAuthType() != ServerConnection.AuthType.PASSWORD) {
                    System.out.println(">>> [자동화] 키 인증 실패. 비밀번호로 접속하여 키를 자동 등록합니다...");
                    registerKeyAutomatically(conn);
                    session.connect(5000);
                } else {
                    throw e;
                }
            }

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();

            channel.connect();

            // ✅ 타임아웃 방식으로 읽기 (readAllBytes 블로킹 문제 해결)
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[4096];
            long start = System.currentTimeMillis();

            while (true) {
                while (in.available() > 0) {
                    int len = in.read(buf);
                    if (len < 0) break;
                    sb.append(new String(buf, 0, len));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    break;
                }
                if (System.currentTimeMillis() - start > 10000) {
                    System.out.println("=== [타임아웃] 10초 초과: " + command);
                    break;
                }
                Thread.sleep(100);
            }

            String result = sb.toString();

            StringBuilder errSb = new StringBuilder();
            while (err.available() > 0) {
                int len = err.read(buf);
                if (len < 0) break;
                errSb.append(new String(buf, 0, len));
            }
            String error = errSb.toString();

            if (!error.isEmpty()) {
                System.out.println("=== STDERR [" + command + "]: " + error);
            }

            channel.disconnect();
            session.disconnect();
            return result.trim();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SSH 연결 실패: " + e.getMessage());
            return "";
        }
    }

    // ✅ 헬퍼 메서드 1: 인증 설정 분리
    private void setupAuth(JSch jsch, ServerConnection conn) throws Exception {
        if (conn.getAuthType() == ServerConnection.AuthType.PEM_PATH) {
            jsch.addIdentity(conn.getPemKeyPath());
        } else if (conn.getAuthType() == ServerConnection.AuthType.PEM_BYTES) {
            jsch.addIdentity("uploaded-key", conn.getPemKeyBytes(), null, null);
        }
    }

    // ✅ 헬퍼 메서드 2: 서버에 내 키를 자동으로 심어주는 로직
    private void registerKeyAutomatically(ServerConnection conn) throws Exception {
        JSch jsch = new JSch();
        Session tempSession = jsch.getSession(conn.getUser(), conn.getHost(), 40022);
//        tempSession.setPassword(conn.getPassword()); // DB의 비밀번호 사용
        tempSession.setPassword("vboxuser1234");



        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password"); // 무조건 비밀번호로만 접속
        tempSession.setConfig(config);
        tempSession.connect(5000);

        // 내 .pem 파일로부터 공개키 문자열 추출 (JSch 기능)
        com.jcraft.jsch.KeyPair kpair = com.jcraft.jsch.KeyPair.load(jsch, conn.getPemKeyPath(), null);
        String publicKey = java.util.Base64.getEncoder().encodeToString(kpair.getPublicKeyBlob());
        String keyLine = "ssh-rsa " + publicKey + " jsch-auto-generated";

        // 서버의 authorized_keys에 등록하는 쉘 명령어
        String script = String.format(
                "mkdir -p ~/.ssh && chmod 700 ~/.ssh && echo '%s' >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys",
                keyLine
        );

        ChannelExec channel = (ChannelExec) tempSession.openChannel("exec");
        channel.setCommand(script);
        channel.connect();
        Thread.sleep(500); // 명령어가 전달될 시간 확보

        channel.disconnect();
        tempSession.disconnect();
        System.out.println(">>> [성공] 서버에 공개키가 등록되었습니다.");
    }

    private String readFirst(ServerConnection conn, String primary, String fallback) {
        System.out.println("=== [readFirst] 연결 시도: " + conn.getHost() + " / user: " + conn.getUser());
        System.out.println("=== [readFirst] authType: " + conn.getAuthType());

        // vboxuser가 adm 그룹인지 먼저 확인
        String groupCheck = execute(conn, "groups");
        System.out.println("=== [그룹 확인]: " + groupCheck);

        String exists = execute(conn, "test -f " + primary + " && echo yes");
        System.out.println("[readFirst] primary=" + primary + " exists=" + exists);

        if (exists.contains("yes")) {
            // 1순위: sg adm 시도
//            String result = execute(conn, "sg adm -c 'tail -n 50 " + primary + "'");
            String result = execute(conn, "echo 'vboxuser1234' | sudo -S tail -n 50 " + primary);
            System.out.println("[readFirst] sg adm 결과 줄 수: " + result.lines().count());

            // 2순위: sg adm 실패 시 일반 tail
            if (result.isEmpty()) {
                System.out.println("[readFirst] sg adm 실패, 일반 tail 시도");
                result = execute(conn, "tail -n 500 " + primary);
                System.out.println("[readFirst] 일반 tail 결과 줄 수: " + result.lines().count());
            }
            return result;
        }

        return execute(conn, fallback.startsWith("/")
                ? "sg adm -c 'tail -n 500 " + fallback + "'"
                : fallback
        );
    }

    // ✅ syslog 분석
    public SyslogSummary analyzeSyslog(String raw) {
        long oomCount      = 0;
        long diskFullCount = 0;
        long crashCount    = 0;
        Map<String, Long> serviceErrors = new HashMap<>();
        List<String> criticalLines      = new ArrayList<>();

        // 포맷: Mar  3 10:00:00 hostname service[pid]: message
        Pattern servicePattern = Pattern.compile("\\w+\\s+\\d+\\s+[\\d:]+\\s+\\S+\\s+(\\S+?)(?:\\[\\d+\\])?:");

        for (String line : raw.lines().toList()) {
            if (line.contains("Out of memory") || line.contains("oom_kill")) {
                oomCount++;
                criticalLines.add(line);
            }
            if (line.contains("No space left on device")) {
                diskFullCount++;
                criticalLines.add(line);
            }
            if (line.contains("failed") || line.contains("crash")) {
                crashCount++;
                Matcher m = servicePattern.matcher(line);
                if (m.find()) serviceErrors.merge(m.group(1), 1L, Long::sum);
            }
        }

        // 에러 많은 서비스 TOP 5
        List<Map.Entry<String, Long>> top5 = serviceErrors.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .toList();

        return new SyslogSummary(oomCount, diskFullCount, crashCount, top5,
                criticalLines.stream().limit(20).toList());
    }

    // ✅ auth.log 분석
    public AuthSummary analyzeAuth(String raw) {
        long failed  = 0, success = 0, sudo = 0, newUser = 0;
        Map<String, Long> failedIps = new HashMap<>();
        Pattern ipPattern = Pattern.compile("from (\\d+\\.\\d+\\.\\d+\\.\\d+)");

        for (String line : raw.lines().toList()) {
            if (line.contains("Failed password")) {
                failed++;
                Matcher m = ipPattern.matcher(line);
                if (m.find()) failedIps.merge(m.group(1), 1L, Long::sum);
            }
            else if (line.contains("Accepted password") || line.contains("Accepted publickey")) {
                success++;
            }
            else if (line.contains("sudo")) sudo++;
            else if (line.contains("new user") || line.contains("useradd")) newUser++;
        }

        // 10회 이상 실패 → 브루트포스 의심
        List<String> suspiciousIps = failedIps.entrySet().stream()
                .filter(e -> e.getValue() >= 10)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> e.getKey() + " (" + e.getValue() + "회 실패)")
                .toList();

        return new AuthSummary(failed, success, sudo, newUser, suspiciousIps);
    }

    // ✅ kern.log 분석
    public KernSummary analyzeKern(String raw) {
        long ioError = 0, hwError = 0, segfault = 0, panic = 0;
        List<String> criticalLines = new ArrayList<>();

        for (String line : raw.lines().toList()) {
            if (line.contains("I/O error") || line.contains("blk_update_request")) {
                ioError++;
                criticalLines.add(line);
            }
            if (line.contains("Hardware Error") || line.contains("EDAC")) {
                hwError++;
                criticalLines.add(line);
            }
            if (line.contains("segfault")) {
                segfault++;
                criticalLines.add(line);
            }
            if (line.contains("Kernel panic")) {
                panic++;
                criticalLines.add(line);
            }
        }

        return new KernSummary(ioError, hwError, segfault, panic,
                criticalLines.stream().limit(20).toList());
    }


    // ============================
    // 전체 분석 한번에 실행
    // ============================
    public FullLogReport analyze(ServerConnection conn) {
        String syslogRaw = readLog(conn, LogType.SYSLOG);
        String authRaw   = readLog(conn, LogType.AUTH);
        String kernRaw   = readLog(conn, LogType.KERNEL);

        System.out.println(syslogRaw);
        System.out.println(authRaw);
        System.out.println(kernRaw);

        return new FullLogReport(
                conn.getHost(),
                analyzeSyslog(syslogRaw),
                analyzeAuth(authRaw),
                analyzeKern(kernRaw)
        );
    }
}