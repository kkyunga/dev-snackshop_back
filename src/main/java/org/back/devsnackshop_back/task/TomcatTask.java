package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TomcatTask implements MiddlewareTask {

    @Override
    public List<String> getPackageInstallCommand(String version, String sudoPrefix) {
        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y tomcat9",
                sudoPrefix + "systemctl enable --now tomcat9"
        );
    }

    @Override
    public List<String> getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String major = (version == null || version.isBlank()) ? "9" : version.split("\\.")[0];
        String ver = (version == null || version.isBlank()) ? "9.0.85" : version;
        String fileName = "apache-tomcat-" + ver + ".tar.gz";
        String url = "https://archive.apache.org/dist/tomcat/tomcat-" + major + "/v" + ver + "/bin/" + fileName;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y wget tar",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path + " && wget -nc " + url,
                "cd " + path + " && tar -zxvf " + fileName + " --strip-components=1",
                "chmod +x " + path + "/bin/*.sh"
        );
    }
}