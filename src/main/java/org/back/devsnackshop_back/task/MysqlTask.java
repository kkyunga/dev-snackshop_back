package org.back.devsnackshop_back.task;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MysqlTask implements MiddlewareTask {

    @Override
    public List<String> getPackageInstallCommand(String version, String sudoPrefix) {
        String pkg = (version == null || version.isBlank())
                ? "mysql-server"
                : "mysql-server-" + version;

        return List.of(
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y " + pkg,
                sudoPrefix + "systemctl enable --now mysql"
        );
    }

    @Override
    public List<String> getBinaryInstallCommand(String path, String version, String sudoPrefix) {
        String ver = (version == null || version.isBlank()) ? "8.0.36" : version;
        String fileName = "mysql-" + ver + "-linux-glibc2.12-x86_64.tar.xz";
        String url = "https://dev.mysql.com/get/Downloads/MySQL-8.0/" + fileName;

        return List.of(
                sudoPrefix + "add-apt-repository universe -y",
                sudoPrefix + "apt-get update",
                sudoPrefix + "apt-get install -y libtinfo6 libncurses6 $(apt-cache show libaio1t64 >/dev/null 2>&1 && echo libaio1t64 || echo libaio1)",
                sudoPrefix + "ln -sf /lib/x86_64-linux-gnu/libtinfo.so.6 /lib/x86_64-linux-gnu/libtinfo.so.5",
                sudoPrefix + "ldconfig",
                sudoPrefix + "mkdir -p " + path,
                sudoPrefix + "chown -R $(whoami):$(whoami) " + path,
                "cd " + path + " && wget -nc " + url,
                "cd " + path + " && tar -Jxvf " + fileName + " --strip-components=1",
                // 👇 실패해도 무조건 실행돼야 하는 영역 (세미콜론 구분 유지)
                sudoPrefix + path + "/bin/mysqld --initialize-insecure --user=mysql ; " +
                        sudoPrefix + "ln -sf " + path + "/bin/mysql /usr/bin/mysql ; " +
                        sudoPrefix + "ln -sf " + path + "/bin/mysqld /usr/bin/mysqld ; " +
                        "hash -r"
        );
    }
}