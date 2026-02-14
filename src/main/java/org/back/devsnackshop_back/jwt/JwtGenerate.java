package org.back.devsnackshop_back.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtGenerate {
    public String encryptionKey (){
        // HS256 알고리즘에 적합한 안전한 랜덤 키 생성
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // 이걸 문자열로 변환해서 출력
        String secretString = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(secretString);
        return secretString;

    }
}
