package com.example.producer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSO 관련 리다이렉트 동작을 구성하기 위한 프로퍼티 바인딩 클래스.
 */
@ConfigurationProperties(prefix = "sso")
public class SsoProperties {

    /**
     * SSO 완료 후 사용자를 이동시킬 프론트엔드 기본 URL.
     */
    private String frontendUrl = "http://localhost:5173/";

    public String getFrontendUrl() {
        return frontendUrl;
    }

    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }
}
