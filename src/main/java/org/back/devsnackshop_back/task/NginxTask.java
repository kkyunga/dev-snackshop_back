package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

@Component
public class NginxTask implements MiddlewareTask{
    @Override
    public String getPackageInstallCommand(String version, String sudoPrefix) {
        String pkg = (version == null || version.isEmpty())
                ? "nginx"
                : "nginx=" + version + "*";

        return String.format(
                "%sapt-get update && " +
                        "%sapt-get install -y %s && " +
                        "%ssystemctl enable --now nginx",
                sudoPrefix, sudoPrefix, pkg, sudoPrefix
        );
    }

    @Override
    public String getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "1.24.0" : version;
        String fileName = "nginx-" + ver + ".tar.gz";
        String url = "http://nginx.org/download/" + fileName;

        return String.join(" && ",

                // 1️⃣ 빌드 도구
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y build-essential libpcre3 libpcre3-dev " +
                        "zlib1g zlib1g-dev openssl libssl-dev",

                // 2️⃣ 작업 디렉토리 생성 + 소유권 보장
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,

                // 3️⃣ 소스 다운로드 (유저)
                "cd " + path,
                "wget -nc " + url,
                "tar -zxvf " + fileName + " --strip-components=1",

                // 4️⃣ 빌드 (유저)
                "./configure --prefix=" + path,
                "make",

                // 5️⃣ 설치 (root)
                sudoPrefix + "make install",

                // 6️⃣ 실행 링크
                sudoPrefix + "ln -sf " + path + "/sbin/nginx /usr/bin/nginx",
                "hash -r"
        );
    }

}
