package com.example.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka 토픽 생성을 담당하는 설정 클래스.
 */
@Configuration
public class KafkaTopicConfig {

    /** 토픽 이름을 설정 파일에서 주입받는다. */
    @Value("${demo.topic-name:demo-topic}")
    private String topicName;

    /**
     * KafkaAdmin이 애플리케이션 시작 시 토픽을 생성할 수 있도록 NewTopic 빈을 등록한다.
     * 3개의 파티션과 3중 복제를 사용해 다중 브로커 환경 분산을 확인할 수 있도록 구성한다.
     * @return 생성할 토픽 정보
     */
    @Bean
    public NewTopic demoTopic() {
        return new NewTopic(topicName, 3, (short) 3);
    }
}
