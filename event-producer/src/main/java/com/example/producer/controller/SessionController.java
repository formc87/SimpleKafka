package com.example.producer.controller;

import com.example.producer.sso.SamlSession;
import com.example.producer.sso.SamlUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping
    public ResponseEntity<SessionResponse> session(HttpSession httpSession) {
        SamlUser user = (SamlUser) httpSession.getAttribute(SamlSession.SESSION_ATTRIBUTE);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SessionResponse(false, null, null));
        }
        return ResponseEntity.ok(new SessionResponse(true, user.getDisplayName(), user.getEmail()));
    }

    public record SessionResponse(boolean authenticated, String displayName, String email) {
    }
}
