package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PythonTask implements MiddlewareTask {

    @Override
    public List<String> getPackageInstallCommand(String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "3.11" : version;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y python" + ver
        );
    }

    @Override
    public List<String> getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "3.11.5" : version;
        String fileName = "Python-" + ver + ".tgz";
        String url = "https://www.python.org/ftp/python/" + ver + "/" + fileName;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y build-essential libssl-dev zlib1g-dev libncurses5-dev libsqlite3-dev libreadline-dev libffi-dev",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path + " && wget -nc " + url,
                "cd " + path + " && tar -zxvf " + fileName + " --strip-components=1",
                "cd " + path + " && ./configure --prefix=" + path + " --enable-optimizations",
                "cd " + path + " && make",
                sudoPrefix + "make install -C " + path,
                sudoPrefix + "ln -sf " + path + "/bin/python3 /usr/bin/python3"
        );
    }
}