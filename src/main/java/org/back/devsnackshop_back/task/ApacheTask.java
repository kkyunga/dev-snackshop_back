package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApacheTask implements MiddlewareTask {

    @Override
    public List<String> getPackageInstallCommand(String version, String sudoPrefix) {
        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y apache2",
                sudoPrefix + "systemctl enable --now apache2"
        );
    }

    @Override
    public List<String> getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "2.4.58" : version;
        String fileName = "httpd-" + ver + ".tar.gz";
        String url = "https://downloads.apache.org/httpd/" + fileName;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y build-essential libpcre3-dev libapr1-dev libaprutil1-dev",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path + " && wget -nc " + url,
                "cd " + path + " && tar -zxvf " + fileName + " --strip-components=1",
                "cd " + path + " && ./configure --prefix=" + path,
                "cd " + path + " && make",
                sudoPrefix + "make install -C " + path,
                sudoPrefix + "ln -sf " + path + "/bin/httpd /usr/bin/httpd"
        );
    }
}