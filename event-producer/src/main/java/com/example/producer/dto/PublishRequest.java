package com.example.producer.dto;

/**
 * REST API로 수신할 메시지 본문을 담는 DTO.
 */
public class PublishRequest {

    /** 전송할 메시지 본문 */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
