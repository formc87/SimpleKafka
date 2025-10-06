package com.example.portal.service;

import com.example.portal.config.ProducerProperties;
import com.example.portal.model.User;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class SamlResponseBuilderTest {

    @Test
    void buildsBase64EncodedSamlResponse() {
        ProducerProperties properties = new ProducerProperties();
        properties.setAcsUrl("http://localhost:8080/saml/acs");
        properties.setRelayState("http://localhost:5173/");
        properties.setIssuer("SimpleSSO");

        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SamlResponseBuilder builder = new SamlResponseBuilder(properties, fixedClock);
        User user = new User("홍길동", "hong@example.com", "password");

        String encoded = builder.build(user);
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        String xml = new String(decodedBytes, StandardCharsets.UTF_8);

        assertThat(xml).contains("hong@example.com");
        assertThat(xml).contains("홍길동");
        assertThat(xml).contains("SimpleSSO");
        assertThat(xml).contains("IssueInstant=\"2024-01-01T00:00:00Z\"");
    }
}
