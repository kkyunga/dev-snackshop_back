package org.back.devsnackshop_back.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.findEmail.FindEmailRequest;
import org.back.devsnackshop_back.dto.findPassword.FindPasswordRequest;
import org.back.devsnackshop_back.dto.login.LoginRequest;
import org.back.devsnackshop_back.dto.login.LoginResponse;
import org.back.devsnackshop_back.dto.resetEmailLink.ResetEmailLinkRequest;
import org.back.devsnackshop_back.dto.updatePassword.UpdatePasswordRequest;
import org.back.devsnackshop_back.dto.user.UserRequest;
import org.back.devsnackshop_back.dto.user.UserResponse;
import org.back.devsnackshop_back.entity.UserEntity;
import org.back.devsnackshop_back.jwt.JwtUtil;
import org.back.devsnackshop_back.mapper.UserMapper;
import org.back.devsnackshop_back.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserMapper userMapper;


    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // 1. 유저 존재 확인 (없으면 바로 예외 던짐)
            UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("존재하지 않는 계정입니다."));
// [중요] DB에 저장된 값과 입력값 직접 비교 로그
            log.info("2. DB 저장된 암호화 비번: {}", user.getPasswordEncrypted());
            log.info("3. 입력 평문 비번: {}", loginRequest.getPassword());


            // 2. Spring Security 인증 매니저를 통한 인증 (비밀번호 대조 등 내부 처리)
            // passwordEncoder.matches()를 직접 호출할 필요 없이 아래 한 줄로 검증됩니다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // 3. 토큰 생성
            String accessToken = jwtUtil.generateToken(authentication.getName());
            String refreshToken = jwtUtil.generateRefreshToken(authentication.getName());



            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(user.getEmail())
                    .build();

        } catch (BadCredentialsException e) {
            // 아이디가 없거나 비밀번호가 틀린 경우
            throw new BadCredentialsException("이메일 또는 비밀번호가 틀렸습니다.");
        } catch (Exception e) {
            log.error("로그인 처리 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("로그인 중 오류가 발생했습니다.");
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

    public UserEntity findEmail(UserRequest userRequest) {
        String name = userRequest.getName();
        String phone = userRequest.getPhone();
        return userRepository.findByNameAndPhone(name, phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));



    }

    public void resetPasswordLink(ResetEmailLinkRequest resetEmailLinkRequest) {
        log.info(resetEmailLinkRequest.toString());
        // 1. URL 생성 (인코딩 포함)
        String resetPasswordUrl = UriComponentsBuilder
                .fromHttpUrl("http://localhost:5173/find-password-result")
                .queryParam("email", resetEmailLinkRequest.getEmail())
                .queryParam("name", resetEmailLinkRequest.getName())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(resetEmailLinkRequest.getEmail());
        mailMessage.setSubject(resetEmailLinkRequest.getEmail()+"비밀번호 리셋 주소입니다");

        // 본문을 조금 더 친절하게 작성
        String content = String.format(
                "안녕하세요 %s님,\n\n비밀번호 재설정을 위한 링크를 보내드립니다.\n아래 링크를 클릭하여 새 비밀번호를 설정해주세요.\n\n%s",
                resetEmailLinkRequest.getName(),
                resetPasswordUrl
        );

        mailMessage.setText(content);

        mailSender.send(mailMessage); // 발송 로직
    }

    public UserResponse findPassword(UserRequest userRequest) {
            log.info(userRequest.toString());
            String name = userRequest.getName();
            String phone = userRequest.getPhone();
            String email = userRequest.getEmail();
            UserEntity userEntity =   userRepository.findByNameAndPhoneAndEmail(name,phone,email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
            UserResponse response =   userMapper.toResponse(userEntity);
            log.info(response.toString());
            return response;
    }
    @Transactional
    public void updatePassword(UserRequest userRequest) {
        UserEntity userEntity = userRepository.findByEmail(userRequest.getEmail()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"사용자를 찾을수 없습니다"));
        String encryptedPassword = passwordEncoder.encode(userRequest.getNewPassword());
        userEntity.setPasswordEncrypted(encryptedPassword);
        userRepository.save(userEntity);
        log.info("사용자 {}의 비밀번호가 성공적으로 암호화되어 변경되었습니다.", userEntity.getEmail());
    }

    public String confirmEmail(UserRequest userRequest) {
        Random random = new Random();
        String formatted = String.format("%06d", random.nextInt(1000000));

        try {
            log.info(userRequest.toString());


            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userRequest.getEmail());
            mailMessage.setSubject(userRequest.getEmail() + "이메일 인증번호");

            // 본문을 조금 더 친절하게 작성
            String content = String.format(
                    "안녕하세요 \n\n이메일 확인을 위한 인증번호를 보내드립니다.\n아래 번호를 확인하여 이메일 인증을 완료해주세요.\n\n%s",
                     formatted
            );

            mailMessage.setText(content);
            mailSender.send(mailMessage); // 발송 로직

        }catch (Exception e) {
            throw new RuntimeException("이메일 발송에 실패했습니다. 관리자에게 문의하세요.", e);
        }
        return formatted;

    }

    public void signup(UserRequest userRequest) {
         log.info(userRequest.toString());
        // 1. 중복 체크 (더 안전한 방식)
        userRepository.findByEmail(userRequest.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        });

        userRepository.findByPhoneNumber(userRequest.getPhone()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 전화번호입니다.");
        });


        try{
             String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
             userRequest.setPassword(encryptedPassword);
             UserEntity  userEntity =   userMapper.toEntity(userRequest);
             userRepository.save(userEntity);

         }catch (Exception e) {
             throw new RuntimeException("회원가입에 실패했습니다. 관리자에게 문의하세요.", e);
         }
    }
}
