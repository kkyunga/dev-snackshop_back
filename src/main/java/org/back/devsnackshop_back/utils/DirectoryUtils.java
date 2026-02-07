package org.back.devsnackshop_back.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DirectoryUtils {
    public static void createDirectoryIfNotExists(String installPath) {
        try {
            ProcessBuilder mkdirPb = new ProcessBuilder(
                    "/usr/bin/sudo", "/bin/mkdir", "-p", installPath
            );
            mkdirPb.redirectErrorStream(true);

            Process mkdirProcess = mkdirPb.start();
            int exitCode = mkdirProcess.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("sudo mkdir 실패, exitCode=" + exitCode);
            }

            // 소유권 변경 (필요 시)
            ProcessBuilder chownPb = new ProcessBuilder(
                    "/usr/bin/sudo", "/bin/chown", "-R", "ubuntu:ubuntu", installPath
            );
            chownPb.start().waitFor();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("sudo 디렉터리 생성 중 오류", e);
        }
    }
}
