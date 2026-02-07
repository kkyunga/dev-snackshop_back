package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.service.ServerManageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/servers")
public class ServerManageController {

    private final ServerManageService serverManageService;

    // POST /api/servers 호출 시 실행됨
    @PostMapping("")
    public ResponseEntity<?> createServer(@RequestBody ServerCreateRequest request) {
        // 서버 생성 로직
        if(request.getIp() == null){
            throw new IllegalArgumentException("IP 주소는 필수입니다");
        }
        serverManageService.createServer(request);
        return ResponseEntity.ok(ApiResponse.success(request));
    }
}
