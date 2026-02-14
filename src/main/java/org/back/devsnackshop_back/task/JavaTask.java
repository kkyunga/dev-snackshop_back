package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JavaTask implements MiddlewareTask {

    // 메이저 버전별 추천 상세 버전 매핑 (2026년 기준 예시)
    private static final Map<String, String> VERSION_MAP = Map.of(
            "8",  "8u402-b06",
            "11", "11.0.22+7",
            "17", "17.0.10+7",
            "21", "21.0.2+13",
            "25", "25+9" // EA(Early Access) 또는 정식 릴리스 버전에 맞춰 수정
    );

    @Override
    public String getPackageInstallCommand(String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "17" : version;
        return String.format("export DEBIAN_FRONTEND=noninteractive && %sapt-get update && %sapt-get install -y openjdk-%s-jdk",
                sudoPrefix, sudoPrefix, ver);
    }

    @Override
    public String getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        // 1. 입력값이 "8", "11" 등 메이저 숫자라면 상세 버전으로 치환, 아니면 입력값 그대로 사용
        String fullVer = VERSION_MAP.getOrDefault(version, (version == null || version.isBlank()) ? "17.0.10+7" : version);

        String url;
        // 2. Java 8은 URL 규칙이 독특하므로 별도 처리
        if (fullVer.startsWith("8")) {
            url = String.format(
                    "https://github.com/adoptium/temurin8-binaries/releases/download/jdk%s/OpenJDK8U-jdk_x64_linux_hotspot_%s.tar.gz",
                    fullVer, fullVer.replace("-", "")
            );
        } else {
            // 3. Java 11 이상 규칙
            String major = fullVer.split("[\\.\\+]")[0];
            url = String.format(
                    "https://github.com/adoptium/temurin%s-binaries/releases/download/jdk-%s/OpenJDK%sU-jdk_x64_linux_hotspot_%s.tar.gz",
                    major, fullVer, major, fullVer.replace("+", "_")
            );
        }

        return String.join(" && ",
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y wget tar",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path,
                "wget -nc " + url + " -O jdk.tar.gz",
                "tar -zxvf jdk.tar.gz --strip-components=1",
                sudoPrefix + "ln -sf " + path + "/bin/java /usr/bin/java",
                sudoPrefix + "ln -sf " + path + "/bin/javac /usr/bin/javac",
                "hash -r"
        );
    }
}