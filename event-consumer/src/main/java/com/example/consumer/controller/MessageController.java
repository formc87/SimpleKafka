package com.example.consumer.controller;

import com.example.consumer.service.MessageStorage;
import com.example.consumer.service.MessageStorage.StoredMessage;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 수신한 메시지를 조회하기 위한 REST 컨트롤러.
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    /** 저장된 메시지를 제공하는 저장소 */
    private final MessageStorage messageStorage;

    public MessageController(MessageStorage messageStorage) {
        this.messageStorage = messageStorage;
    }

    /**
     * 저장된 메시지 전체를 반환한다.
     * @return 메시지 목록
     */
    @GetMapping
    public List<StoredMessage> list() {
        return messageStorage.getMessages();
    }
}
