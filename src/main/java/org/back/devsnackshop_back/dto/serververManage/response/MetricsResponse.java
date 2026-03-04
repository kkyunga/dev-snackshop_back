package org.back.devsnackshop_back.dto.serververManage.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MetricsResponse {
    private LocalDateTime timestamp;
    private Double cpuUsage;
    private Integer cpuCores;
    private Integer cpuThreads;
    private Double memoryUsed;
    private Double memoryTotal;
    private Double memoryPercentage;
    private String diskUsed;
    private String diskTotal;
    private Integer diskUsedGb;
    private Integer diskTotalGb;
    private Double diskPercentage;
    private Double networkRxKb;
    private Double networkTxKb;
}
