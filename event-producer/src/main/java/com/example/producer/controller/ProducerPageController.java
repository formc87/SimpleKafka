package com.example.producer.controller;

import com.example.producer.sso.SamlSession;
import com.example.producer.sso.SamlUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProducerPageController {

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        SamlUser user = (SamlUser) session.getAttribute(SamlSession.SESSION_ATTRIBUTE);
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/producer")
    public String producer(Model model, HttpSession session) {
        SamlUser user = (SamlUser) session.getAttribute(SamlSession.SESSION_ATTRIBUTE);
        if (user == null) {
            session.setAttribute("ssoError", "SSO 세션이 만료되었거나 존재하지 않습니다.");
            return "redirect:/sso-error";
        }
        model.addAttribute("user", user);
        return "producer";
    }

    @GetMapping("/sso-error")
    public String ssoError(Model model, HttpSession session) {
        Object error = session.getAttribute("ssoError");
        if (error != null) {
            model.addAttribute("error", error);
            session.removeAttribute("ssoError");
        }
        return "sso-error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
