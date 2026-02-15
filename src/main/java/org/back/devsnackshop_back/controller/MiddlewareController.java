package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.dto.middlewareManage.InstallRequest;
import org.back.devsnackshop_back.dto.middlewareManage.response.MiddlewareListResponse;
import org.back.devsnackshop_back.service.MiddlewareService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/middleware")
@RequiredArgsConstructor
public class MiddlewareController {
    private final MiddlewareService middlewareService;

    @PostMapping(value = "/install", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> requestInstall(@ModelAttribute InstallRequest dto) {
        if (dto.getMiddlewares().size() >= 2 && (dto.getMwVersion() != null && !dto.getMwVersion().isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("error", "미들웨어가 2개 이상일 때 버전을 지정할 수 없습니다."));
        }

        String taskId = UUID.randomUUID().toString();

        middlewareService.installMiddlewaresAsync(taskId, dto);

        return ResponseEntity.accepted().body(Map.of(
                "taskId", taskId,
                "message", "설치 작업이 백그라운드에서 시작되었습니다."
        ));
    }

    @GetMapping("/install/status/{taskId}")
    public ResponseEntity<?> getStatus(@PathVariable String taskId) {
        return ResponseEntity.ok(middlewareService.getStatus(taskId));
    }

    @GetMapping("/list")
    public ResponseEntity<?> middlewareList(@RequestParam("userOsId") long userOsId) {
        try {
            if (userOsId <= 0) {
                throw new IllegalArgumentException("사용자 OS ID를 입력해주세요.");
            }
            List<MiddlewareListResponse> result = middlewareService.middlewareList(userOsId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
