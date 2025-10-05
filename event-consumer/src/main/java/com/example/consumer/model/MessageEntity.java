package com.example.consumer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Kafka에서 수신한 메시지를 데이터베이스에 저장하기 위한 엔티티.
 */
@Entity
@Table(name = "messages")
public class MessageEntity {

    /** 자동 증가 기본 키 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 메시지 본문 */
    @Column(nullable = false, length = 2000)
    private String payload;

    /** 메시지 수신 시각 */
    @Column(nullable = false)
    private Instant receivedAt;

    protected MessageEntity() {
        // JPA가 사용할 기본 생성자
    }

    public MessageEntity(String payload, Instant receivedAt) {
        this.payload = payload;
        this.receivedAt = receivedAt;
    }

    public Long getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
