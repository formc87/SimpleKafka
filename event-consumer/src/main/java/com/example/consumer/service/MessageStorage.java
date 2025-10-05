package com.example.consumer.service;

import com.example.consumer.model.MessageEntity;
import com.example.consumer.repository.MessageRepository;
import java.time.Instant;
import java.util.List;
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

    public MessageStorage(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * 새로운 메시지를 저장한다.
     * @param payload 수신한 메시지 본문
     */
    @Transactional
    public void add(String payload) {
        MessageEntity entity = new MessageEntity(payload, Instant.now());
        messageRepository.save(entity);
    }

    /**
     * 저장된 메시지 목록을 최신순으로 반환한다.
     * @return 메시지 목록
     */
    @Transactional(readOnly = true)
    public List<StoredMessage> getMessages() {
        return messageRepository.findAllByOrderByReceivedAtDesc().stream()
                .map(entity -> new StoredMessage(entity.getId(), entity.getReceivedAt(), entity.getPayload()))
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

        public StoredMessage(Long id, Instant receivedAt, String payload) {
            this.id = id;
            this.receivedAt = receivedAt;
            this.payload = payload;
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
    }
}
