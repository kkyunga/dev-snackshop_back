package org.back.devsnackshop_back.common;

import lombok.Getter;

@Getter
public enum ResultHttpCode {

    SUCCESS(200,"성공"),
    INVALID_PARAMETER(400,"파라미터 확인 부탁드립니다"),
    UNAUTHORIZED(401, "인증되지 않은 사용자입니다"), // 추가

    SERVER_ERROR(500,"서버 에러입니다");

    private final int resultCode;
    private final  String message;


    ResultHttpCode(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;

    }
}
