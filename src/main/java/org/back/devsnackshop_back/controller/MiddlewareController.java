package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.dto.middlewareManage.InstallRequest;
import org.back.devsnackshop_back.dto.middlewareManage.response.MiddlewareUserOsListResponse;
import org.back.devsnackshop_back.service.InstallMiddlewareService;
import org.back.devsnackshop_back.service.MiddlewareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/middleware")
@RequiredArgsConstructor
public class MiddlewareController {
    private final MiddlewareService middlewareService;
    private final InstallMiddlewareService installMiddleware;

    @PostMapping(value = "/install")
    public CompletableFuture<ResponseEntity<?>> requestInstall(@RequestBody InstallRequest dto) {
        return installMiddleware.installMiddleware(dto)
                .handle((result, ex) -> {
                    if (ex != null) {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        return (ResponseEntity<?>) ResponseEntity.internalServerError()
                                .body(Map.of("error", "설치 중 오류가 발생했습니다: " + cause.getMessage()));
                    }
                    return (ResponseEntity<?>) ResponseEntity.ok(result);
                });
    }

    @GetMapping("/simple/list")
    public ResponseEntity<?> simpleMiddlewareList() {
        return ResponseEntity.ok(middlewareService.simpleMiddlewareList());
    }

    @GetMapping("/list")
    public ResponseEntity<?>  middlewareList() {
        return ResponseEntity.ok(middlewareService.middlewareList());
    }

    @GetMapping("/user-os/list")
    public ResponseEntity<?> middlewareUserOsList(@RequestParam("userOsId") long userOsId) {
        try {
            if (userOsId <= 0) {
                throw new IllegalArgumentException("사용자 OS ID를 입력해주세요.");
            }
            List<MiddlewareUserOsListResponse> result = middlewareService.middlewareUserOsList(userOsId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/install/status")
    public ResponseEntity<?> getInstallStatus(@RequestParam Long userOsId) {
        return ResponseEntity.ok(middlewareService.getInstallStatus(userOsId));
    }
}
