package com.example.portal.controller;

import com.example.portal.model.User;
import com.example.portal.service.SamlResponseBuilder;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortalController {

    private final SamlResponseBuilder samlResponseBuilder;

    public PortalController(SamlResponseBuilder samlResponseBuilder) {
        this.samlResponseBuilder = samlResponseBuilder;
    }

    @GetMapping("/portal")
    public String portal(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "portal";
    }

    @GetMapping("/sso/producer")
    public String ssoRedirect(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("acsUrl", samlResponseBuilder.acsUrl());
        model.addAttribute("relayState", samlResponseBuilder.relayState());
        model.addAttribute("samlResponse", samlResponseBuilder.build(user));
        return "saml-post";
    }
}
