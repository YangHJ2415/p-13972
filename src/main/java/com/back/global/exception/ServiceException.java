package com.back.global.exception;

import com.back.global.rsData.RsData;

public class ServiceException extends RuntimeException { // 서비스 예외 처리 클래스
    private final String resultCode;
    private final String msg;

    public ServiceException(String resultCode, String msg) { // 생성자, 예외 코드와 메시지를 받아 초기화
        super(resultCode + " : " + msg); // 예외 메시지를 설정
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public RsData<Void> getRsData() { // 예외 발생 시 RsData 형태로 응답을 반환
        return new RsData<>(resultCode, msg, null);
    }
}