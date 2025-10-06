package com.example.producer.controller;

import com.example.producer.sso.SamlService;
import com.example.producer.sso.SamlSession;
import com.example.producer.sso.SamlUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/saml")
public class SamlController {

    private final SamlService samlService;

    public SamlController(SamlService samlService) {
        this.samlService = samlService;
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
        session.setAttribute(SamlSession.SESSION_ATTRIBUTE, samlUser.get());
        if (relayState != null && relayState.startsWith("/")) {
            return "redirect:" + relayState;
        }
        return "redirect:/producer";
    }
}
