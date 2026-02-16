package org.back.devsnackshop_back.exception;

import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.common.ResultHttpCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. 보안/인증 예외: 로그인 실패 (아이디/비번 불일치)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e) {
        log.warn("로그인 실패: {}", e.getMessage());
        // ResultHttpCode에 AUTH_FAILED 같은 코드가 있다면 교체하세요.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "이메일 또는 비밀번호가 일치하지 않습니다."));
    }

    /**
     * 2. 보안/인증 예외: 그 외 인증 관련 모든 예외
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.error("인증 예외 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, "인증에 실패하였습니다."));
    }

    /**
     * 3. 비즈니스 예외: 잘못된 인자 값 전달
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e){
        log.warn("잘못된 인자: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ResultHttpCode.INVALID_PARAMETER));
    }

    /**
     * 4. 데이터베이스 예외: 중복 데이터 저장 시도 등 (Unique 제약 조건 위반)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("데이터 제약 조건 위반: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, "이미 사용 중인 정보이거나 데이터 처리 중 충돌이 발생했습니다."));
    }

    /**
     * 5. 스프링 프레임워크 예외: ResponseStatusException (명시적 상태 코드 던짐)
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(ApiResponse.error(e.getStatusCode().value(), e.getReason()));
    }

    /**
     * 6. 기타 지원하지 않는 HTTP Method 호출 (GET인데 POST 등)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(405, "지원하지 않는 요청 방식입니다."));
    }

    /**
     * 7. 최후의 보루: 처리되지 않은 모든 예외 (500 에러)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception e){
        log.error("서버 내부 에러 발생!", e); // 서버 로그에 상세 에러 출력
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ResultHttpCode.SERVER_ERROR));
    }
}