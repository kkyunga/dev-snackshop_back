package org.back.devsnackshop_back.entity.elastic;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;
@Document(indexName = "log-analyze")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAnalyzeDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long serverId;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime collectedAt;

    @Field(type = FieldType.Object)
    private SystemLog system;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemLog {
        @Field(type = FieldType.Keyword) private String osType;
        @Field(type = FieldType.Keyword) private String osVersion;
        @Field(type = FieldType.Object)  private SyslogInfo syslog;
        @Field(type = FieldType.Object)  private AuthInfo auth;
        @Field(type = FieldType.Object)  private KernelInfo kernel;

        @JsonProperty("package")
        @Field(type = FieldType.Object, name = "package")
        private PackageInfo pkg;

        @Field(type = FieldType.Object)  private SystemdInfo systemd;
        @Field(type = FieldType.Object)  private DmesgInfo dmesg;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyslogInfo {
        @Field(type = FieldType.Integer) private Integer errorCount;
        @Field(type = FieldType.Integer) private Integer oomCount;
        @Field(type = FieldType.Integer) private Integer cronFail;
        @Field(type = FieldType.Integer) private Integer serviceFail;
        @Field(type = FieldType.Nested)  private List<ErrorEntry> topErrors;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthInfo {
        @Field(type = FieldType.Integer) private Integer authFail;
        @Field(type = FieldType.Integer) private Integer authSuccess;
        @Field(type = FieldType.Integer) private Integer invalidUser;
        @Field(type = FieldType.Integer) private Integer rootLogin;
        @Field(type = FieldType.Integer) private Integer sudoUsage;
        @Field(type = FieldType.Nested)  private List<AttackIp> topAttackIps;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KernelInfo {
        @Field(type = FieldType.Integer) private Integer errorCount;
        @Field(type = FieldType.Integer) private Integer segfaultCount;
        @Field(type = FieldType.Integer) private Integer diskErrorCount;
        @Field(type = FieldType.Nested)  private List<ErrorEntry> topErrors;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageInfo {
        @Field(type = FieldType.Integer) private Integer installCount;
        @Field(type = FieldType.Integer) private Integer removeCount;
        @Field(type = FieldType.Integer) private Integer errorCount;
        @Field(type = FieldType.Nested)  private List<PackageEntry> recentList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemdInfo {
        @Field(type = FieldType.Integer) private Integer failedCount;
        @Field(type = FieldType.Nested)  private List<SystemdUnit> failedUnits;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DmesgInfo {
        @Field(type = FieldType.Integer) private Integer errorCount;
        @Field(type = FieldType.Nested)  private List<ErrorEntry> topErrors;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorEntry {
        @Field(type = FieldType.Integer) private Integer count;
        @Field(type = FieldType.Text)    private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttackIp {
        @Field(type = FieldType.Keyword) private String ip;
        @Field(type = FieldType.Integer) private Integer count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageEntry {
        @Field(type = FieldType.Keyword) private String action;
        @Field(type = FieldType.Keyword) private String packageName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemdUnit {
        @Field(type = FieldType.Keyword) private String unit;
        @Field(type = FieldType.Keyword) private String state;
    }
}