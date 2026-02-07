package org.back.devsnackshop_back.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MiddlewareTaskFactory {
    private final MysqlTask mysqlTask;
    private final NginxTask nginxTask;

    public MiddlewareTask getTask(String middlewareName) {
        middlewareName = middlewareName.toLowerCase();

        if (middlewareName.equals("mysql")) return mysqlTask;
        if (middlewareName.equals("nginx")) return nginxTask;
        throw new IllegalArgumentException("지원하지 않는 미들웨어입니다: " + middlewareName);
    }
}
