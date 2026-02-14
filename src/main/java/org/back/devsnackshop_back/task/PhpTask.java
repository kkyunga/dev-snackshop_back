package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

@Component
public class PhpTask implements MiddlewareTask {
    @Override
    public String getPackageInstallCommand(String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "8.2" : version;
        return String.format("%sapt-get update && %sapt-get install -y php%s php%s-cli php%s-fpm",
                sudoPrefix, sudoPrefix, ver, ver, ver);
    }

    @Override
    public String getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "8.2.15" : version;
        String url = "https://www.php.net/distributions/php-" + ver + ".tar.gz";

        return String.join(" && ",
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y build-essential libxml2-dev sqlite3 libsqlite3-dev",
                sudoPrefix + "mkdir -p " + path,
                "cd " + path,
                "wget -nc " + url,
                "tar -zxvf php-" + ver + ".tar.gz --strip-components=1",
                "./configure --prefix=" + path + " --enable-fpm",
                "make",
                sudoPrefix + "make install"
        );
    }
}