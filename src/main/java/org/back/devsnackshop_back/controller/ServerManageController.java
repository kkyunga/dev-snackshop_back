package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.dto.serververManage.response.ServerListResponse;
import org.back.devsnackshop_back.service.ServerManageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/list")
    public ResponseEntity<?> serverList(@RequestParam("userId") long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("사용자 ID를 입력해주세요.");
        }
        List<ServerListResponse> result = serverManageService.serverList(userId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

}
