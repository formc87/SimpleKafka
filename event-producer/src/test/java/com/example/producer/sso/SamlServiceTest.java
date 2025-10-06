package com.example.producer.sso;

import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SamlServiceTest {

    @Test
    void parseResponseReturnsUserWhenAttributesPresent() {
        String xml = """
                <saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">
                  <saml2:Assertion>
                    <saml2:Subject>
                      <saml2:NameID>hong@example.com</saml2:NameID>
                    </saml2:Subject>
                    <saml2:AttributeStatement>
                      <saml2:Attribute Name=\"displayName\">
                        <saml2:AttributeValue>홍길동</saml2:AttributeValue>
                      </saml2:Attribute>
                      <saml2:Attribute Name=\"email\">
                        <saml2:AttributeValue>hong@example.com</saml2:AttributeValue>
                      </saml2:Attribute>
                    </saml2:AttributeStatement>
                  </saml2:Assertion>
                </saml2p:Response>
                """;
        String encoded = Base64.getEncoder().encodeToString(xml.getBytes());

        SamlService samlService = new SamlService();
        Optional<SamlUser> samlUser = samlService.parseResponse(encoded);

        assertThat(samlUser).isPresent();
        assertThat(samlUser.get().getDisplayName()).isEqualTo("홍길동");
        assertThat(samlUser.get().getEmail()).isEqualTo("hong@example.com");
    }

    @Test
    void parseResponseReturnsEmptyWhenMissingNameId() {
        String xml = """
                <saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">
                  <saml2:Assertion>
                    <saml2:AttributeStatement>
                      <saml2:Attribute Name=\"displayName\">
                        <saml2:AttributeValue>홍길동</saml2:AttributeValue>
                      </saml2:Attribute>
                    </saml2:AttributeStatement>
                  </saml2:Assertion>
                </saml2p:Response>
                """;
        String encoded = Base64.getEncoder().encodeToString(xml.getBytes());

        SamlService samlService = new SamlService();
        Optional<SamlUser> samlUser = samlService.parseResponse(encoded);

        assertThat(samlUser).isEmpty();
    }
}
