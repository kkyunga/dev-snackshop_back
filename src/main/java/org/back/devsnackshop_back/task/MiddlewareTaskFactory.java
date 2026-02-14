package org.back.devsnackshop_back.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MiddlewareTaskFactory {
    private final MysqlTask mysqlTask;
    private final NginxTask nginxTask;
    private final JavaTask javaTask;
    private final ApacheTask apacheTask;
    private final NodejsTask nodejsTask;
    private final PhpTask phpTask;
    private final PythonTask pythonTask;
    private final TomcatTask tomcatTask;

    public MiddlewareTask getTask(String middlewareName) {
        middlewareName = middlewareName.toLowerCase();

        if (middlewareName.equals("mysql")) return mysqlTask;
        if (middlewareName.equals("nginx")) return nginxTask;
        if (middlewareName.equals("java")) return javaTask;
        if (middlewareName.equals("apache")) return apacheTask;
        if (middlewareName.equals("nodejs")) return nodejsTask;
        if (middlewareName.equals("php")) return phpTask;
        if (middlewareName.equals("python")) return pythonTask;
        if (middlewareName.equals("tomcat")) return tomcatTask;
        throw new IllegalArgumentException("지원하지 않는 미들웨어입니다: " + middlewareName);
    }
}
