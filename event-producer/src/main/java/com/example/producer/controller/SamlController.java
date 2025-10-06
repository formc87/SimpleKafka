package com.example.producer.controller;

import com.example.producer.config.SsoProperties;
import com.example.producer.sso.SamlService;
import com.example.producer.sso.SamlSession;
import com.example.producer.sso.SamlUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Controller
@RequestMapping("/saml")
public class SamlController {

    private final SamlService samlService;
    private final SsoProperties ssoProperties;

    public SamlController(SamlService samlService, SsoProperties ssoProperties) {
        this.samlService = samlService;
        this.ssoProperties = ssoProperties;
    }

    @PostMapping("/acs")
    public String acs(@RequestParam("SAMLResponse") String samlResponse,
                      @RequestParam(value = "RelayState", required = false) String relayState,
                      HttpSession session) {
        Optional<SamlUser> samlUser = samlService.parseResponse(samlResponse);
        if (samlUser.isEmpty()) {
            session.setAttribute("ssoError", "SAML 응답을 처리할 수 없습니다.");
            return "redirect:/sso-error";
        }
        SamlUser user = samlUser.get();
        session.setAttribute(SamlSession.SESSION_ATTRIBUTE, user);
        return "redirect:" + resolveRedirectTarget(relayState, user);
    }

    private String resolveRedirectTarget(String relayState, SamlUser user) {
        if (relayState != null && !relayState.isBlank()) {
            if (relayState.startsWith("http://") || relayState.startsWith("https://")) {
                return appendUserParams(UriComponentsBuilder.fromUriString(relayState), user);
            }
            if (relayState.startsWith("/")) {
                return appendUserParams(UriComponentsBuilder.fromPath(relayState), user);
            }
        }
        return appendUserParams(UriComponentsBuilder.fromUriString(ssoProperties.getFrontendUrl()), user);
    }

    private String appendUserParams(UriComponentsBuilder builder, SamlUser user) {
        return builder
                .replaceQueryParam("displayName", user.getDisplayName())
                .replaceQueryParam("email", user.getEmail())
                .build()
                .encode()
                .toUriString();
    }
}
