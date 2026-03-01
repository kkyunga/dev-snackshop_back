package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NodejsTask implements MiddlewareTask {

    @Override
    public List<String> getPackageInstallCommand(String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "20" : version;

        return List.of(
                "curl -fsSL https://deb.nodesource.com/setup_" + ver + ".x | " + sudoPrefix + "bash -",
                sudoPrefix + "apt-get install -y nodejs"
        );
    }

    @Override
    public List<String> getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "v20.11.0" : version;
        String fileName = "node-" + ver + "-linux-x64.tar.xz";
        String url = "https://nodejs.org/dist/" + ver + "/" + fileName;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y wget tar",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path + " && wget -nc " + url,
                "cd " + path + " && tar -Jxvf " + fileName + " --strip-components=1",
                sudoPrefix + "ln -sf " + path + "/bin/node /usr/bin/node",
                sudoPrefix + "ln -sf " + path + "/bin/npm /usr/bin/npm"
        );
    }
}