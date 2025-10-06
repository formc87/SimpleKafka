package com.example.producer.sso;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Optional;

@Service
public class SamlService {

    private static final String ASSERTION_NS = "urn:oasis:names:tc:SAML:2.0:assertion";

    public Optional<SamlUser> parseResponse(String base64Response) {
        if (base64Response == null || base64Response.isBlank()) {
            return Optional.empty();
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Response);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(decodedBytes));

            NodeList nameIdNodes = document.getElementsByTagNameNS(ASSERTION_NS, "NameID");
            NodeList attributeNodes = document.getElementsByTagNameNS(ASSERTION_NS, "Attribute");

            if (nameIdNodes.getLength() == 0) {
                return Optional.empty();
            }
            String nameId = nameIdNodes.item(0).getTextContent();
            String displayName = null;
            String email = null;

            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Element element = (Element) attributeNodes.item(i);
                String name = element.getAttribute("Name");
                NodeList values = element.getElementsByTagNameNS(ASSERTION_NS, "AttributeValue");
                if (values.getLength() == 0) {
                    continue;
                }
                String value = values.item(0).getTextContent();
                if ("displayName".equals(name)) {
                    displayName = value;
                } else if ("email".equals(name)) {
                    email = value;
                }
            }

            if (displayName == null) {
                displayName = nameId;
            }
            if (email == null) {
                email = nameId;
            }

            return Optional.of(new SamlUser(nameId, displayName, email));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
