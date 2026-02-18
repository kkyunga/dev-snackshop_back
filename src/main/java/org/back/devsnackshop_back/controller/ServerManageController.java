package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;
import org.back.devsnackshop_back.dto.serververManage.response.ServerListResponse;
import org.back.devsnackshop_back.service.ServerManageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/servers")
public class ServerManageController {

    private final ServerManageService serverManageService;



    @GetMapping("/list")
    public ResponseEntity<?> serverList(Authentication authentication) {
        List<ServerListResponse> result = serverManageService.serverList(authentication);
        return ResponseEntity.ok(ApiResponse.success(result));
    }



    @PostMapping(value = "/validate-key", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> validateKeyFile(Authentication authentication,
                                             @RequestPart("keyFile") MultipartFile file) {

        if(file == null || file.isEmpty())
        {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,"파일이 없습니다."));
        }

        try{
            log.info("수신된 키 파일명: {}", file.getOriginalFilename());
            boolean result = serverManageService.validateKeyFile(file,authentication);
            return ResponseEntity.ok(ApiResponse.success(result));
        }catch (Exception e) {
            log.error("파일 처리 중 오류 발생: ", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "서버 내부 오류로 파일을 처리할 수 없습니다."));
        }


    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createServer(
            @RequestPart("request") ServerCreateRequest serverCreateRequest,
            @RequestPart(value = "keyFile", required = false) MultipartFile keyFile,
            Authentication authentication) {
        if (serverCreateRequest.getIp() == null) {
            throw new IllegalArgumentException("IP 주소는 필수입니다");
        }

        log.info(serverCreateRequest.toString());
        serverManageService.createServer(serverCreateRequest, keyFile, authentication);
        return ResponseEntity.ok(ApiResponse.success("서버가 추가되었습니다."));
    }



    @GetMapping(value = "/serverSpecItems")
    public ResponseEntity<?> serverSpecItem() {
        HashMap<String, Object> specs =serverManageService.getServerSpecItems();
        log.info(specs.toString());
        return ResponseEntity.ok(ApiResponse.success(specs));
     }





}
