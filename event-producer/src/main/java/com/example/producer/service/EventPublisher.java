package com.example.producer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * KafkaTemplate을 사용하여 메시지를 전송하는 서비스 클래스.
 */
@Service
public class EventPublisher {

    /** 사용할 KafkaTemplate */
    private final KafkaTemplate<String, String> kafkaTemplate;

    /** 기본 전송 토픽 이름 */
    private final String topicName;

    public EventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                          @Value("${demo.topic-name:demo-topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    /**
     * 지정된 메시지를 Kafka로 전송한다.
     * @param message 전송할 메시지
     */
    public void send(String message) {
        kafkaTemplate.send(topicName, message);
    }
}
