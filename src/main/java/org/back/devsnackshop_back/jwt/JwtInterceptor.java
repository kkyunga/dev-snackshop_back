package org.back.devsnackshop_back.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            // 토큰 유효성 검증 로직 추가

            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 2. 응답 타입 설정 (JSON)
        response.setContentType("application/json;charset=UTF-8");
        // 3. 직접 메시지 작성
        response.getWriter().write("{\"message\": \"토큰이 없거나 유효하지 않습니다.\"}");
        return false;
    }
}
