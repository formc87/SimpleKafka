package com.example.portal.service;

import com.example.portal.config.ProducerProperties;
import com.example.portal.model.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Component
public class SamlResponseBuilder {

    private static final String SAML_PROTOCOL_NS = "urn:oasis:names:tc:SAML:2.0:protocol";
    private static final String SAML_ASSERTION_NS = "urn:oasis:names:tc:SAML:2.0:assertion";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final ProducerProperties properties;
    private final Clock clock;

    @org.springframework.beans.factory.annotation.Autowired
    public SamlResponseBuilder(ProducerProperties properties) {
        this(properties, Clock.systemUTC());
    }

    SamlResponseBuilder(ProducerProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public String build(User user) {
        Instant now = clock.instant();
        String responseId = "_" + UUID.randomUUID();
        String assertionId = "_" + UUID.randomUUID();
        String issueInstant = FORMATTER.format(now);

        String xml = """
                <saml2p:Response xmlns:saml2p=\"%s\" xmlns:saml2=\"%s\" ID=\"%s\" Version=\"2.0\" IssueInstant=\"%s\">
                  <saml2:Issuer>%s</saml2:Issuer>
                  <saml2p:Status>
                    <saml2p:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\" />
                  </saml2p:Status>
                  <saml2:Assertion ID=\"%s\" IssueInstant=\"%s\" Version=\"2.0\">
                    <saml2:Issuer>%s</saml2:Issuer>
                    <saml2:Subject>
                      <saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\">%s</saml2:NameID>
                    </saml2:Subject>
                    <saml2:AttributeStatement>
                      <saml2:Attribute Name=\"displayName\">
                        <saml2:AttributeValue>%s</saml2:AttributeValue>
                      </saml2:Attribute>
                      <saml2:Attribute Name=\"email\">
                        <saml2:AttributeValue>%s</saml2:AttributeValue>
                      </saml2:Attribute>
                    </saml2:AttributeStatement>
                  </saml2:Assertion>
                </saml2p:Response>
                """.formatted(SAML_PROTOCOL_NS, SAML_ASSERTION_NS, responseId, issueInstant,
                properties.getIssuer(), assertionId, issueInstant, properties.getIssuer(),
                user.getEmail(), user.getName(), user.getEmail());

        return Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8));
    }

    public String relayState() {
        return properties.getRelayState();
    }

    public String acsUrl() {
        return properties.getAcsUrl();
    }
}
