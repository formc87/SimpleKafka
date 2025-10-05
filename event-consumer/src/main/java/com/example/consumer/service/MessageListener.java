package com.example.consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka 토픽을 구독하여 수신한 메시지를 저장소에 적재하고 로그를 남기는 리스너.
 */
@Component
public class MessageListener {

    private static final Logger log = LoggerFactory.getLogger(MessageListener.class);

    /** 수신 메시지를 저장할 저장소 */
    private final MessageStorage messageStorage;

    public MessageListener(MessageStorage messageStorage) {
        this.messageStorage = messageStorage;
    }

    /**
     * KafkaListener 어노테이션으로 demo-topic을 구독한다.
     * @param payload 수신된 메시지 본문
     */
    @KafkaListener(topics = "${demo.topic-name:demo-topic}", groupId = "demo-consumer")
    public void listen(String payload) {
        log.info("Kafka에서 메시지를 수신했습니다: {}", payload);
        messageStorage.add(payload);
    }
}
