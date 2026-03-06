package org.back.devsnackshop_back.dto.elastic.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LogAnalyzeRequest {

    private Long serverId;
    private SystemLog system;

    // ============================================================
    // System
    // ============================================================

    @Data
    public static class SystemLog {
        private String osType;
        private String osVersion;

        private SyslogInfo syslog;
        private AuthInfo auth;
        private KernelInfo kernel;

        @JsonProperty("package")
        private PackageInfo pkg;

        private SystemdInfo systemd;
        private DmesgInfo dmesg;
    }

    @Data
    public static class SyslogInfo {
        private int errorCount;
        private int oomCount;
        private int cronFail;
        private int serviceFail;
        private List<ErrorEntry> topErrors;
    }

    @Data
    public static class AuthInfo {
        private int authFail;
        private int authSuccess;
        private int invalidUser;
        private int rootLogin;
        private int sudoUsage;
        private List<AttackIp> topAttackIps;
    }

    @Data
    public static class KernelInfo {
        private int errorCount;
        private int segfaultCount;
        private int diskErrorCount;
        private List<ErrorEntry> topErrors;
    }

    @Data
    public static class PackageInfo {
        private int installCount;
        private int removeCount;
        private int errorCount;
        private List<PackageEntry> recentList;
    }

    @Data
    public static class SystemdInfo {
        private int failedCount;
        private List<SystemdUnit> failedUnits;
    }

    @Data
    public static class DmesgInfo {
        private int errorCount;
        private List<ErrorEntry> topErrors;
    }

    // ============================================================
    // 공통 내부 클래스
    // ============================================================

    @Data
    public static class ErrorEntry {
        private int count;
        private String message;
    }

    @Data
    public static class AttackIp {
        private String ip;
        private int count;
    }

    @Data
    public static class PackageEntry {
        private String action;        // install / remove
        private String packageName;
    }

    @Data
    public static class SystemdUnit {
        private String unit;
        private String state;
    }
}