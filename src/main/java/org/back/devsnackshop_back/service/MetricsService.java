package org.back.devsnackshop_back.service;

import lombok.RequiredArgsConstructor;
import org.back.devsnackshop_back.dto.elastic.request.MetricsRequest;
import org.back.devsnackshop_back.dto.serververManage.response.MetricsResponse;
import org.back.devsnackshop_back.entity.elastic.ServerMetricsDocument;
import org.back.devsnackshop_back.repository.ServerMetricsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final ServerMetricsRepository serverMetricsRepository;

        public void save(MetricsRequest request) {

            serverMetricsRepository.save(ServerMetricsDocument.builder()
                    .serverId(request.getServerId())
                    .cpuUsage(request.getCpuUsage())
                    .cpuCores(request.getCpuCores())
                    .cpuThreads(request.getCpuThreads())
                    .memoryUsed(request.getMemoryUsed())
                    .memoryTotal(request.getMemoryTotal())
                    .memoryPercentage(request.getMemoryPercentage())
                    .diskUsed(request.getDiskUsed())
                    .diskTotal(request.getDiskTotal())
                    .diskUsedGb(request.getDiskUsedGb())
                    .diskTotalGb(request.getDiskTotalGb())
                    .diskPercentage(request.getDiskPercentage())
                    .timestamp(LocalDateTime.now())
                    .networkRxKb(request.getNetworkRxKb())
                    .networkTxKb(request.getNetworkTxKb())
                    .build());
        }
    @Transactional
    public List<MetricsResponse> getMetrics(Long serverId) {
        return serverMetricsRepository.findByServerIdOrderByTimestampAsc(serverId)
                .stream()
                .map(doc -> MetricsResponse.builder()
                        .timestamp(doc.getTimestamp())
                        .cpuUsage(doc.getCpuUsage())
                        .cpuCores(doc.getCpuCores())
                        .cpuThreads(doc.getCpuThreads())
                        .memoryUsed(doc.getMemoryUsed())
                        .memoryTotal(doc.getMemoryTotal())
                        .memoryPercentage(doc.getMemoryPercentage())
                        .diskUsed(doc.getDiskUsed())
                        .diskTotal(doc.getDiskTotal())
                        .diskUsedGb(doc.getDiskUsedGb())
                        .diskTotalGb(doc.getDiskTotalGb())
                        .diskPercentage(doc.getDiskPercentage())
                        .networkRxKb(doc.getNetworkRxKb())   // 추가
                        .networkTxKb(doc.getNetworkTxKb())   // 추가
                        .build())
                .collect(Collectors.toList());
    }
}
