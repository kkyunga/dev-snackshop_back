package org.back.devsnackshop_back.task;

public interface MiddlewareTask {
    // 패키지 설치 명령어 생성
    String getPackageInstallCommand(String version, String sudoPrefix);

    // 바이너리(.tar) 설치 명령어 생성
    String getBinaryInstallCommand(String path, String version, String sudoPrefix);
}
