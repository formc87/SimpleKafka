package com.example.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 자동 발행 기능을 설정 파일과 바인딩하기 위한 프로퍼티 클래스.
 */
@ConfigurationProperties(prefix = "demo.auto-publish")
public class AutoPublishProperties {

    /** 자동 발행 기능 사용 여부 */
    private boolean enabled = false;

    /** 발행 간격(밀리초) */
    private long interval = 5000L;

    /** 발행에 사용할 메시지 템플릿 */
    private String messageTemplate = "자동 발행 메시지 - %d";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
}
