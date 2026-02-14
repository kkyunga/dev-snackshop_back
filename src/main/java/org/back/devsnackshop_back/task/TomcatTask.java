package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

@Component
public class TomcatTask implements MiddlewareTask {
    @Override
    public String getPackageInstallCommand(String version, String sudoPrefix) {
        return String.format("%sapt-get update && %sapt-get install -y tomcat9", sudoPrefix, sudoPrefix);
    }

    @Override
    public String getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String major = (version == null || version.isBlank()) ? "9" : version.split("\\.")[0];
        String ver = (version == null || version.isBlank()) ? "9.0.85" : version;
        String url = String.format("https://archive.apache.org/dist/tomcat/tomcat-%s/v%s/bin/apache-tomcat-%s.tar.gz",
                major, ver, ver);

        return String.join(" && ",
                sudoPrefix + "mkdir -p " + path,
                "cd " + path,
                "wget -nc " + url,
                "tar -zxvf apache-tomcat-" + ver + ".tar.gz --strip-components=1",
                "chmod +x " + path + "/bin/*.sh"
        );
    }
}