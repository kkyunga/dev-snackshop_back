package org.back.devsnackshop_back.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.dto.findEmail.FindEmailRequest;
import org.back.devsnackshop_back.dto.login.LoginRequest;
import org.back.devsnackshop_back.dto.login.LoginResponse;
import org.back.devsnackshop_back.entity.UserEntity;
import org.back.devsnackshop_back.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        LoginResponse loginResponse =authService.login(loginRequest);

// 2. Refresh Token은 쿠키에 담아서 보안 강화 (HttpOnly)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false) // 로컬 환경은 false, 배포(HTTPS) 환경은 true
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // 3. Access Token과 유저 정보만 JSON으로 응답
        return ResponseEntity.ok(Map.of(
                "accessToken", loginResponse.getAccessToken(),
                "email", loginResponse.getEmail()
        ));

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
    public ResponseEntity<?> findEmail(@RequestBody FindEmailRequest findEmailRequest) {
        UserEntity userEntity = authService.findEmail(findEmailRequest);
        return ResponseEntity.ok(ApiResponse.success(userEntity));
    }






}
