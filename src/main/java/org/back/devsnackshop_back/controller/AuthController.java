package org.back.devsnackshop_back.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.dto.findEmail.FindEmailRequest;
import org.back.devsnackshop_back.dto.findPassword.FindPasswordRequest;
import org.back.devsnackshop_back.dto.login.LoginRequest;
import org.back.devsnackshop_back.dto.login.LoginResponse;
import org.back.devsnackshop_back.dto.resetEmailLink.ResetEmailLinkRequest;
import org.back.devsnackshop_back.dto.serververManage.response.ServerListResponse;
import org.back.devsnackshop_back.dto.updatePassword.UpdatePasswordRequest;
import org.back.devsnackshop_back.dto.user.UserRequest;
import org.back.devsnackshop_back.entity.UserEntity;
import org.back.devsnackshop_back.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest);

        // Refresh Token 쿠키 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false) // HTTPS 환경이면 true로 변경
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // 응답 데이터 구성
        Map<String, Object> data = Map.of(
                "accessToken", loginResponse.getAccessToken(),
                "email", loginResponse.getEmail()
        );

        // ApiResponse.success() 또는 성공 규격에 맞춰 응답
        return ResponseEntity.ok(ApiResponse.success(data));
    }



    @PostMapping("/tokenFactory")
    public ResponseEntity<?> tokenFactory(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,HttpServletResponse response) {

        // 서비스 호출하여 새 Access Token 생성
        String newAccessToken = authService.refreshAccessToken(refreshToken,response);

        // 성공 시 새 Access Token 반환
        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }


    @PostMapping("/findEmail")
    public ResponseEntity<?> findEmail(@RequestBody UserRequest userRequest) {
        UserEntity userEntity = authService.findEmail(userRequest);
        return ResponseEntity.ok(ApiResponse.success(userEntity));
    }



    @PostMapping("/reset-password-link")
    public ResponseEntity<?> resetPasswordLink(@RequestBody ResetEmailLinkRequest resetEmailLinkRequest) {
        authService.resetPasswordLink(resetEmailLinkRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 메일을 발송했습니다."));
    }

    @PostMapping("/findPassword")
    public ResponseEntity<?> findPassword(@RequestBody UserRequest userRequest) {
        log.info(userRequest.toString());
        authService.findPassword(userRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 메일을 발송했습니다."));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UserRequest userRequest) {
        authService.updatePassword(userRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정을 완료했습니다."));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequest userRequest) {
        authService.signup(userRequest);
        return ResponseEntity.ok(ApiResponse.success("회원가입을 완료했습니다!."));
    }



    @PostMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmail(@RequestBody UserRequest userRequest) {
        String confirmEmailNumber = authService.confirmEmail(userRequest);
        return ResponseEntity.ok(ApiResponse.success(confirmEmailNumber));
    }
}
