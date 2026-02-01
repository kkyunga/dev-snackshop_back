package org.back.devsnackshop_back.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    //http 상태코드
    private int status;
    //응답 메세지
    private String message;
    //실제 결과 데이터
    private T data;


    public static <T> ApiResponse<T> success(ResultHttpCode code,T data){
        return ApiResponse.<T>builder()
                .status(code.getResultCode())
                .message(code.getMessage())
                .data(data)
                .build();
    }


    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(ResultHttpCode.SUCCESS.getResultCode())
                .message(ResultHttpCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }




    public static <T> ApiResponse<T> error(ResultHttpCode code){
        return ApiResponse.<T>builder()
                .status(code.getResultCode())
                .message(code.getMessage())
                .data(null)
                .build();
    }



    public static <T> ApiResponse<T> error(ResultHttpCode code,String message){
        return ApiResponse.<T>builder()
                .status(code.getResultCode())
                .message(message)
                .data(null)
                .build();
    }





}
