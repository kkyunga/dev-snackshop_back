package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

@Component
public class PythonTask implements MiddlewareTask {
    @Override
    public String getPackageInstallCommand(String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "3.11" : version;
        return String.format("%sapt-get update && %sapt-get install -y python%s", sudoPrefix, sudoPrefix, ver);
    }

    @Override
    public String getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "3.11.5" : version;
        String url = "https://www.python.org/ftp/python/" + ver + "/Python-" + ver + ".tgz";

        return String.join(" && ",
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y build-essential libssl-dev zlib1g-dev libncurses5-dev libsqlite3-dev libreadline-dev libffi-dev",
                sudoPrefix + "mkdir -p " + path,
                "cd " + path,
                "wget -nc " + url,
                "tar -zxvf Python-" + ver + ".tgz --strip-components=1",
                "./configure --prefix=" + path + " --enable-optimizations",
                "make",
                sudoPrefix + "make install",
                sudoPrefix + "ln -sf " + path + "/bin/python3 /usr/bin/python3"
        );
    }
}