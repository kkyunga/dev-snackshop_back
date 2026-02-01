package org.back.devsnackshop_back.exception;

import org.back.devsnackshop_back.common.ApiResponse;
import org.back.devsnackshop_back.common.ResultHttpCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ResultHttpCode.INVALID_PARAMETER));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ResultHttpCode.SERVER_ERROR));
    }
}
