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

    /** 메시지를 받은 토픽 이름 */
    @Column(nullable = false, length = 255)
    private String topic;

    /** 메시지가 속한 파티션 번호 */
    @Column(nullable = false)
    private Integer partition;

    /** 파티션 내 오프셋 값 */
    @Column(name = "record_offset", nullable = false)
    private Long offset;

    /** 메시지를 전달한 리더 브로커 id (조회 실패 시 null) */
    @Column(name = "leader_broker_id")
    private Integer leaderBrokerId;

    /** 리더 브로커 호스트명 */
    @Column(name = "leader_host", length = 255)
    private String leaderHost;

    /** 리더 브로커 포트 */
    @Column(name = "leader_port")
    private Integer leaderPort;

    protected MessageEntity() {
        // JPA가 사용할 기본 생성자
    }

    public MessageEntity(String payload, Instant receivedAt, String topic, Integer partition, Long offset,
            Integer leaderBrokerId, String leaderHost, Integer leaderPort) {
        this.payload = payload;
        this.receivedAt = receivedAt;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.leaderBrokerId = leaderBrokerId;
        this.leaderHost = leaderHost;
        this.leaderPort = leaderPort;
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

    public String getTopic() {
        return topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public Long getOffset() {
        return offset;
    }

    public Integer getLeaderBrokerId() {
        return leaderBrokerId;
    }

    public String getLeaderHost() {
        return leaderHost;
    }

    public Integer getLeaderPort() {
        return leaderPort;
    }
}
