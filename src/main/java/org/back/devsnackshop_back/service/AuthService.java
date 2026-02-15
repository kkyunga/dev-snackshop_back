package org.back.devsnackshop_back.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.findEmail.FindEmailRequest;
import org.back.devsnackshop_back.dto.login.LoginRequest;
import org.back.devsnackshop_back.dto.login.LoginResponse;
import org.back.devsnackshop_back.entity.UserEntity;
import org.back.devsnackshop_back.jwt.JwtUtil;
import org.back.devsnackshop_back.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public LoginResponse login(LoginRequest loginRequest) {
        try {

            UserEntity user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
//
//            if (user != null) {
//                // passwordEncoder.matches(평문비번, 해시비번)
//                boolean isMatch = passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordEncrypted());
//
//                log.info("입력한 비번: {}", loginRequest.getPassword());
//                log.info("DB의 해시값: {}", user.getPasswordEncrypted());
//                log.info("일치 여부 확인: {}", isMatch);
//            }



            // 1. 입력받은 이메일/비번으로 '인증용 토큰' 생성 (아직 검증 전)
            System.out.println("비밀번호 암호회 :::"+new BCryptPasswordEncoder().encode(loginRequest.getPassword()));
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            String accessToken = jwtUtil.generateToken(authentication.getName());
            String refreshToken = jwtUtil.generateRefreshToken(authentication.getName());
//            user.setRefreshToken(refreshToken);
//            userRepository.save(user);


            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(user.getEmail())
                    .build();



        } catch (AuthenticationException e) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 틀렸습니다.");
        }
    }

    @Transactional // DB 저장을 위해 트랜잭션 유지 권장
    public String refreshAccessToken(String refreshToken, HttpServletResponse response) {

        // 1. 검증 및 유저 조회 (생략 - 작성하신 코드와 동일)
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 세션이 만료되었습니다.");
        }
        String email = jwtUtil.getEmailFromToken(refreshToken);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
//
//        // 2. 중복 로그인 체크 (작성하신 코드와 동일)
//        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
//            log.warn("중복 로그인 감지: {} 유저의 이전 세션 토큰 무효화", email);
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "다른 기기에서 로그인되어 접속이 종료되었습니다.");
//        }

        // 3. [Access Token 새로 만들기] -> 변수에 딱 한 번만 담기
        String newAccessToken = jwtUtil.generateToken(email);

        // 4. [Refresh Token 만료 임박 체크]
        long remainingTime = jwtUtil.getExpiration(refreshToken);
        long twoDaysInMs = 2 * 24 * 60 * 60 * 1000L;

        if (remainingTime < twoDaysInMs) {
            log.info("Refresh Token 만료 임박 - 새 토큰 발급 및 DB 갱신");
            String newRefreshToken = jwtUtil.generateRefreshToken(email);

//            user.setRefreshToken(newRefreshToken);
//            userRepository.save(user);

            ResponseCookie newCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());
        }

        // 5. 최종적으로 위에서 만든 newAccessToken을 반환!
        return newAccessToken;
    }

    public UserEntity findEmail(FindEmailRequest findEmailRequest) {
        String name = findEmailRequest.getName();
        String phone = findEmailRequest.getPhone();
        return userRepository.findByNameAndPhone(name, phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));



    }
}
