package org.back.devsnackshop_back.dto.elastic.request;


import lombok.Data;

@Data
    public class MetricsRequest {
        private Long serverId;
        private Double cpuUsage;
        private Double memoryUsed;
        private Double memoryTotal;
        private Double memoryPercentage;
        private String diskUsed;
        private String diskTotal;
        private Double diskPercentage;
    }

