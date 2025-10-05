package com.example.producer.controller;

import com.example.producer.dto.PublishRequest;
import com.example.producer.service.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST 엔드포인트를 통해 메시지를 수동 발행할 수 있는 컨트롤러.
 */
@RestController
@RequestMapping("/publish")
public class PublishController {

    private static final Logger log = LoggerFactory.getLogger(PublishController.class);

    /** Kafka로 메시지를 전송하는 서비스 */
    private final EventPublisher eventPublisher;

    public PublishController(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * POST 요청으로 전달받은 메시지를 Kafka로 전송한다.
     * @param request 발행 요청 본문
     * @return 성공 응답
     */
    @PostMapping
    public ResponseEntity<String> publish(@RequestBody PublishRequest request) {
        eventPublisher.send(request.getMessage());
        log.info("수동 발행 요청을 처리했습니다: {}", request.getMessage());
        return ResponseEntity.ok("메시지를 발행했습니다.");
    }
}
