package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

@Component
public class ApacheTask implements MiddlewareTask {
    @Override
    public String getPackageInstallCommand(String version, String sudoPrefix) {
        return String.format("%sapt-get update && %sapt-get install -y apache2", sudoPrefix, sudoPrefix);
    }

    @Override
    public String getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "2.4.58" : version;
        String url = "https://downloads.apache.org/httpd/httpd-" + ver + ".tar.gz";

        return String.join(" && ",
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y build-essential libpcre3-dev libapr1-dev libaprutil1-dev",
                sudoPrefix + "mkdir -p " + path,
                "cd " + path,
                "wget -nc " + url,
                "tar -zxvf httpd-" + ver + ".tar.gz --strip-components=1",
                "./configure --prefix=" + path,
                "make",
                sudoPrefix + "make install"
        );
    }
}