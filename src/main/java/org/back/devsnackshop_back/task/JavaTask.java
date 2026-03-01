package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JavaTask implements MiddlewareTask {

    private static final Map<String, String> VERSION_MAP = Map.of(
            "8",  "8u402-b06",
            "11", "11.0.22+7",
            "17", "17.0.10+7",
            "21", "21.0.2+13",
            "25", "25+9"
    );

    @Override
    public List<String> getPackageInstallCommand(String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "17" : version;

        return List.of(
                "export DEBIAN_FRONTEND=noninteractive",
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y openjdk-" + ver + "-jdk"
        );
    }

    @Override
    public List<String> getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String fullVer = VERSION_MAP.getOrDefault(version, (version == null || version.isBlank()) ? "17.0.10+7" : version);

        String url;
        if (fullVer.startsWith("8")) {
            url = String.format(
                    "https://github.com/adoptium/temurin8-binaries/releases/download/jdk%s/OpenJDK8U-jdk_x64_linux_hotspot_%s.tar.gz",
                    fullVer, fullVer.replace("-", "")
            );
        } else {
            String major = fullVer.split("[\\.\\+]")[0];
            url = String.format(
                    "https://github.com/adoptium/temurin%s-binaries/releases/download/jdk-%s/OpenJDK%sU-jdk_x64_linux_hotspot_%s.tar.gz",
                    major, fullVer, major, fullVer.replace("+", "_")
            );
        }

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y wget tar",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path + " && wget -nc " + url + " -O jdk.tar.gz",
                "cd " + path + " && tar -zxvf jdk.tar.gz --strip-components=1",
                sudoPrefix + "ln -sf " + path + "/bin/java /usr/bin/java",
                sudoPrefix + "ln -sf " + path + "/bin/javac /usr/bin/javac",
                "hash -r"
        );
    }
}