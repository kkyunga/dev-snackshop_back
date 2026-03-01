package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.dto.middlewareManage.InstallRequest;
import org.back.devsnackshop_back.dto.middlewareManage.response.MiddlewareListResponse;
import org.back.devsnackshop_back.service.InstallMiddlewareService;
import org.back.devsnackshop_back.service.MiddlewareService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/middleware")
@RequiredArgsConstructor
public class MiddlewareController {
    private final MiddlewareService middlewareService;
    private final InstallMiddlewareService installMiddleware;

    @PostMapping(value = "/install")
    public ResponseEntity<?> requestInstall(@RequestBody InstallRequest dto) {
        installMiddleware.installMiddleware(dto);

        return ResponseEntity.accepted().body(Map.of(
                "message", "설치 작업이 백그라운드에서 시작되었습니다."
        ));
    }

    @GetMapping("/simple/list")
    public ResponseEntity<?> simpleMiddlewareList() {
        return ResponseEntity.ok(middlewareService.simpleMiddlewareList());
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
