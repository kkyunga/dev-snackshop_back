package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NginxTask implements MiddlewareTask {

    @Override
    public List<String> getPackageInstallCommand(String version, String sudoPrefix) {
        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y nginx",
                sudoPrefix + "systemctl enable --now nginx"
        );
    }

    @Override
    public List<String> getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "1.24.0" : version;
        String fileName = "nginx-" + ver + ".tar.gz";
        String url = "http://nginx.org/download/" + fileName;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y build-essential libpcre3 libpcre3-dev zlib1g zlib1g-dev openssl libssl-dev",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path + " && wget -nc " + url,  // cd는 &&로 묶어야 경로 유지됨
                "cd " + path + " && tar -zxvf " + fileName + " --strip-components=1",
                "cd " + path + " && ./configure --prefix=" + path,
                "cd " + path + " && make",
                sudoPrefix + "make install -C " + path,
                sudoPrefix + "ln -sf " + path + "/sbin/nginx /usr/bin/nginx"
        );
    }
}
