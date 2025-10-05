package com.example.consumer.service;

import com.example.consumer.model.MessageEntity;
import com.example.consumer.repository.MessageRepository;
import com.example.consumer.service.ClusterMetadataService.BrokerInfo;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수신한 메시지를 데이터베이스에 보관하고 REST API로 제공하는 서비스.
 */
@Service
public class MessageStorage {

    /** 데이터베이스와 통신하는 리포지토리 */
    private final MessageRepository messageRepository;
    /** 클러스터 메타데이터 조회 서비스 */
    private final ClusterMetadataService clusterMetadataService;

    public MessageStorage(MessageRepository messageRepository, ClusterMetadataService clusterMetadataService) {
        this.messageRepository = messageRepository;
        this.clusterMetadataService = clusterMetadataService;
    }

    /**
     * 새로운 메시지를 저장한다.
     * @param payload   수신한 메시지 본문
     * @param topic     수신 토픽
     * @param partition 파티션 번호
     * @param offset    파티션 내 오프셋
     */
    @Transactional
    public void add(String payload, String topic, int partition, long offset) {
        Optional<BrokerInfo> leaderInfo = clusterMetadataService.findLeader(topic, partition);
        MessageEntity entity = new MessageEntity(
                payload,
                Instant.now(),
                topic,
                partition,
                offset,
                leaderInfo.map(BrokerInfo::id).orElse(null),
                leaderInfo.map(BrokerInfo::host).orElse(null),
                leaderInfo.map(BrokerInfo::port).orElse(null));
        messageRepository.save(entity);
    }

    /**
     * 저장된 메시지 목록을 최신순으로 반환한다.
     * @return 메시지 목록
     */
    @Transactional(readOnly = true)
    public List<StoredMessage> getMessages() {
        return messageRepository.findAllByOrderByReceivedAtDesc().stream()
                .map(entity -> new StoredMessage(
                        entity.getId(),
                        entity.getReceivedAt(),
                        entity.getPayload(),
                        entity.getTopic(),
                        entity.getPartition(),
                        entity.getOffset(),
                        entity.getLeaderBrokerId(),
                        entity.getLeaderHost(),
                        entity.getLeaderPort()))
                .collect(Collectors.toList());
    }

    /**
     * 메시지와 수신 시간을 함께 표현하는 간단한 내부 DTO 클래스.
     */
    public static class StoredMessage {
        /** 메시지 식별자 */
        private final Long id;
        /** 메시지 수신 시각 */
        private final Instant receivedAt;
        /** 메시지 본문 */
        private final String payload;
        /** 토픽 이름 */
        private final String topic;
        /** 파티션 번호 */
        private final Integer partition;
        /** 오프셋 */
        private final Long offset;
        /** 리더 브로커 id */
        private final Integer leaderBrokerId;
        /** 리더 브로커 호스트 */
        private final String leaderHost;
        /** 리더 브로커 포트 */
        private final Integer leaderPort;

        public StoredMessage(Long id, Instant receivedAt, String payload, String topic, Integer partition, Long offset,
                Integer leaderBrokerId, String leaderHost, Integer leaderPort) {
            this.id = id;
            this.receivedAt = receivedAt;
            this.payload = payload;
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

        public Instant getReceivedAt() {
            return receivedAt;
        }

        public String getPayload() {
            return payload;
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
}
