package com.example.consumer.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Kafka AdminClient를 빈으로 제공해 메타데이터 조회에 활용하기 위한 설정.
 */
@Configuration
public class KafkaAdminConfig {

    /**
     * 스프링 부트가 자동으로 구성한 KafkaAdmin의 설정을 기반으로 AdminClient를 생성한다.
     * @param kafkaAdmin Kafka 클러스터 접속 정보를 담고 있는 관리자 빈
     * @return 애플리케이션 전역에서 재사용할 AdminClient
     */
    @Bean(destroyMethod = "close")
    public AdminClient kafkaAdminClient(KafkaAdmin kafkaAdmin) {
        return AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }
}
