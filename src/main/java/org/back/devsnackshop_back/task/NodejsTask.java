package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

@Component
public class NodejsTask implements MiddlewareTask {
    @Override
    public String getPackageInstallCommand(String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "20" : version;
        return String.format("curl -fsSL https://deb.nodesource.com/setup_%s.x | %sbash - && %sapt-get install -y nodejs",
                ver, sudoPrefix, sudoPrefix);
    }

    @Override
    public String getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "v20.11.0" : version;
        String url = "https://nodejs.org/dist/" + ver + "/node-" + ver + "-linux-x64.tar.xz";

        return String.join(" && ",
                sudoPrefix + "mkdir -p " + path,
                "cd " + path,
                "wget -nc " + url,
                "tar -Jxvf node-" + ver + "-linux-x64.tar.xz --strip-components=1",
                sudoPrefix + "ln -sf " + path + "/bin/node /usr/bin/node",
                sudoPrefix + "ln -sf " + path + "/bin/npm /usr/bin/npm"
        );
    }
}