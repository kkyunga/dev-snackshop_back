package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.elastic.request.MetricsRequest;
import org.back.devsnackshop_back.dto.serververManage.response.MetricsResponse;
import org.back.devsnackshop_back.entity.elastic.ServerMetricsDocument;
import org.back.devsnackshop_back.service.MetricsService;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/metrics")
public class MetricsController{
    private final MetricsService metricsService;
    @PostMapping
    public ResponseEntity<?> receiveMetrics(@RequestBody MetricsRequest request) {
        metricsService.save(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/server/latest")
    public ResponseEntity<?> getServerMetrics(@RequestParam Long serverId) {
        try {
            return ResponseEntity.ok(metricsService.getMetrics(serverId));
        } catch (NoSuchIndexException e) {
            return ResponseEntity.ok(List.of());
        }
    }




}
