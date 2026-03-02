package org.back.devsnackshop_back.dto.elastic.request;


import lombok.Data;

@Data
public class MetricsRequest {
    private Long serverId;
    private Double cpuUsage;
    private Integer cpuCores;       // 추가
    private Integer cpuThreads;     // 추가
    private Double memoryUsed;
    private Double memoryTotal;
    private Double memoryPercentage;
    private String diskUsed;
    private String diskTotal;
    private Integer diskUsedGb;     // 추가
    private Integer diskTotalGb;    // 추가
    private Double diskPercentage;
}