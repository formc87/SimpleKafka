package com.example.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Kafka 메시지를 구독하는 컨슈머 서버의 진입점.
 */
@SpringBootApplication
public class ConsumerApplication {

    /**
     * 스프링 부트 애플리케이션을 실행한다.
     * @param args 커맨드라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
