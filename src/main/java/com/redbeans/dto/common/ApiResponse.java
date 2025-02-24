package com.redbeans.dto.common;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter //생성자 어노테이션
@Builder

//API 형식 일반화 클래스
public class ApiResponse<T>{

    private final boolean success;//응답 성공여부 boolean
    private final T data;//실제 데이터 T
    private final String error;// 에러 메세지
    private final LocalDateTime timestamp;//response 시간 기록

    //성공 응답 생성 static 메서드
    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    //에러 응답 생성 static 메서드


    public static <T> ApiResponse<T> error(String error){
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
