package org.back.devsnackshop_back.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    // Access: 30분, Refresh: 7일
    private final long accessTokenExpiration = 1000 * 60 * 30;
    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 7;

    public JwtUtil() {
        // 실제 운영시는 application.yml의 고정 키를 사용 권장
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(String email) {
        return createToken(email, accessTokenExpiration);
    }

    public String generateRefreshToken(String email) {
        return createToken(email, refreshTokenExpiration);
    }

    private String createToken(String email, long expirationTime) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpiration(String refreshToken) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        // 만료 시간(exp) - 현재 시간(now) = 남은 시간(ms)
        return claims.getExpiration().getTime() - System.currentTimeMillis();



    }
}