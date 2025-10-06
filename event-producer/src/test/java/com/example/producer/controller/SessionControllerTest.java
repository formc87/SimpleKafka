package com.example.producer.controller;

import com.example.producer.sso.SamlSession;
import com.example.producer.sso.SamlUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("로그인 세션이 있으면 사용자 정보를 반환한다")
    void returnsSessionUser() throws Exception {
        SamlUser samlUser = new SamlUser("hong@example.com", "홍길동", "hong@example.com");

        mockMvc.perform(get("/api/session")
                        .sessionAttr(SamlSession.SESSION_ATTRIBUTE, samlUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.displayName").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hong@example.com"));
    }

    @Test
    @DisplayName("로그인 세션이 없으면 401을 반환한다")
    void returnsUnauthorizedWhenSessionMissing() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.authenticated").value(false));
    }
}
