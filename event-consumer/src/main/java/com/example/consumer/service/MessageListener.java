package com.example.consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
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
     * @param payload   수신된 메시지 본문
     * @param topic     수신 토픽 이름
     * @param partition 메시지를 받은 파티션 번호
     * @param offset    파티션 내 오프셋
     */
    @KafkaListener(topics = "${demo.topic-name:demo-topic}", groupId = "demo-consumer")
    public void listen(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Kafka에서 메시지를 수신했습니다: payload={}, topic={}, partition={}, offset={}",
                payload, topic, partition, offset);
        messageStorage.add(payload, topic, partition, offset);
    }
}
