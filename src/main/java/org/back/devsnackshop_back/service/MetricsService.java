package org.back.devsnackshop_back.service;

import lombok.RequiredArgsConstructor;
import org.back.devsnackshop_back.dto.elastic.request.MetricsRequest;
import org.back.devsnackshop_back.entity.elastic.ServerMetricsDocument;
import org.back.devsnackshop_back.repository.ServerMetricsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final ServerMetricsRepository serverMetricsRepository;

        public void save(MetricsRequest request) {

            serverMetricsRepository.save(ServerMetricsDocument.builder()
                    .serverId(request.getServerId())
                    .cpuUsage(request.getCpuUsage())
                    .memoryUsed(request.getMemoryUsed())
                    .memoryTotal(request.getMemoryTotal())
                    .memoryPercentage(request.getMemoryPercentage())
                    .diskUsed(request.getDiskUsed())
                    .diskTotal(request.getDiskTotal())
                    .diskPercentage(request.getDiskPercentage())
                    .timestamp(LocalDateTime.now())
                    .build());
        }

}
