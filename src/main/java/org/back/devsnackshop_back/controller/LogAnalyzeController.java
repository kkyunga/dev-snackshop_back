package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.elastic.request.LogAnalyzeRequest;
import org.back.devsnackshop_back.dto.elastic.request.MetricsRequest;
import org.back.devsnackshop_back.entity.elastic.LogAnalyzeDocument;
import org.back.devsnackshop_back.service.LogAnalyzeService;
import org.back.devsnackshop_back.service.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogAnalyzeController {
    private final LogAnalyzeService logAnalyzeService;
    //crontab 에 sudo 권한으로 저장하자 그럼 데이터 나옴
    @PostMapping("/analyze")
    public ResponseEntity<?> receiveMetrics(@RequestBody LogAnalyzeRequest request) {
        try {
            logAnalyzeService.save(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[LogAnalyze] 저장 실패 - serverId: {}, error: {}", request.getServerId(), e.getMessage());
            return ResponseEntity.internalServerError().body("저장 실패: " + e.getMessage());
        }
    }



    @GetMapping("/analyze/{serverId}")
    public ResponseEntity<?> receiveMetrics(@PathVariable long serverId) {
        try {
            LogAnalyzeDocument result = logAnalyzeService.findLogDataByServerId(serverId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[LogAnalyze] 로그 데이터 가져오기 실패 - serverId: {}, error: {}", serverId, e.getMessage());
            return ResponseEntity.internalServerError().body("저장 실패: " + e.getMessage());
        }
    }


    @GetMapping("/analyze/{serverId}/history")
    public ResponseEntity<?> getLogAnalyzeHistory(
            @PathVariable Long serverId,
            @RequestParam(defaultValue = "60") int minutes) {
        try {
            return ResponseEntity.ok(logAnalyzeService.getHistory(serverId, minutes));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("조회 실패: " + e.getMessage());
        }
    }




}
