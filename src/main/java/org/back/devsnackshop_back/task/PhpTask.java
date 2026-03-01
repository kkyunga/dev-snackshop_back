package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PhpTask implements MiddlewareTask {

    @Override
    public List<String> getPackageInstallCommand(String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "8.2" : version;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y php" + ver + " php" + ver + "-cli php" + ver + "-fpm",
                sudoPrefix + "systemctl enable --now php" + ver + "-fpm"
        );
    }

    @Override
    public List<String> getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "8.2.15" : version;
        String fileName = "php-" + ver + ".tar.gz";
        String url = "https://www.php.net/distributions/" + fileName;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y build-essential libxml2-dev sqlite3 libsqlite3-dev",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path + " && wget -nc " + url,
                "cd " + path + " && tar -zxvf " + fileName + " --strip-components=1",
                "cd " + path + " && ./configure --prefix=" + path + " --enable-fpm",
                "cd " + path + " && make",
                sudoPrefix + "make install -C " + path,
                sudoPrefix + "ln -sf " + path + "/bin/php /usr/bin/php"
        );
    }
}