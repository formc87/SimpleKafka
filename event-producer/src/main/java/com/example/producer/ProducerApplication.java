package com.example.producer;

import com.example.producer.config.AutoPublishProperties;
import com.example.producer.config.SsoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 애플리케이션 엔트리 포인트로, 프로듀서 서버를 실행하고 스케줄링 및 설정 바인딩을 활성화한다.
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({AutoPublishProperties.class, SsoProperties.class})
public class ProducerApplication {

    /**
     * 스프링 부트 애플리케이션을 시작한다.
     * @param args 커맨드라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }
}
