package org.back.devsnackshop_back.entity.elastic;

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

@Document(indexName = "server-metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerMetricsDocument {
    @Id
    private String id;

    private Long serverId;
    private Double cpuUsage;
    private Double memoryUsed;
    private Double memoryTotal;
    private Double memoryPercentage;
    private String diskUsed;
    private String diskTotal;
    private Double diskPercentage;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime timestamp;
}