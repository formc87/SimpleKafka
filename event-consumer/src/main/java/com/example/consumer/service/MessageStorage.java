package com.example.consumer.service;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 수신한 메시지를 메모리에 보관하여 REST API로 조회할 수 있도록 돕는 저장소.
 */
@Component
public class MessageStorage {

    /** 조회 시 최신 순으로 반환하기 위해 연결 리스트 사용 */
    private final LinkedList<StoredMessage> messages = new LinkedList<>();

    /** 최대 보관 개수 (무한 성장 방지) */
    private final int maxSize = 100;

    /**
     * 새로운 메시지를 저장한다.
     * @param payload 수신한 메시지 본문
     */
    public synchronized void add(String payload) {
        messages.addFirst(new StoredMessage(Instant.now(), payload));
        while (messages.size() > maxSize) {
            messages.removeLast();
        }
    }

    /**
     * 저장된 메시지 목록을 읽기 전용 리스트로 반환한다.
     * @return 메시지 목록
     */
    public synchronized List<StoredMessage> getMessages() {
        return Collections.unmodifiableList(List.copyOf(messages));
    }

    /**
     * 메시지와 수신 시간을 함께 표현하는 간단한 내부 클래스.
     * (record를 사용하면 더 간결하지만, 주석 예시를 위해 클래스로 구현)
     */
    public static class StoredMessage {
        /** 메시지 수신 시각 */
        private final Instant receivedAt;
        /** 메시지 본문 */
        private final String payload;

        public StoredMessage(Instant receivedAt, String payload) {
            this.receivedAt = receivedAt;
            this.payload = payload;
        }

        public Instant getReceivedAt() {
            return receivedAt;
        }

        public String getPayload() {
            return payload;
        }
    }
}
