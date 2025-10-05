package com.example.producer.service;

import com.example.producer.config.AutoPublishProperties;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 설정에 따라 일정 주기로 메시지를 자동 발행하는 컴포넌트.
 */
@Component
public class AutoPublishService {

    private static final Logger log = LoggerFactory.getLogger(AutoPublishService.class);

    /** 자동 발행 설정 값 */
    private final AutoPublishProperties properties;

    /** 메시지 일련번호 생성을 위한 카운터 */
    private final AtomicLong counter = new AtomicLong();

    /** 실제 Kafka 전송을 담당하는 서비스 */
    private final EventPublisher eventPublisher;

    public AutoPublishService(AutoPublishProperties properties, EventPublisher eventPublisher) {
        this.properties = properties;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 설정된 간격에 따라 자동 발행을 수행한다.
     * fixedDelayString을 사용해 설정 파일에서 간격을 제어한다.
     */
    @Scheduled(fixedDelayString = "${demo.auto-publish.interval:5000}")
    public void publishAutomatically() {
        if (!properties.isEnabled()) {
            return;
        }
        long sequence = counter.incrementAndGet();
        String message = properties.getMessageTemplate().formatted(sequence);
        eventPublisher.send(message);
        log.info("자동으로 메시지를 발행했습니다: {}", message);
    }
}
