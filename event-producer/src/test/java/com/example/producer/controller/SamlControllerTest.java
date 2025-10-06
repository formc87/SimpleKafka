package com.example.producer.controller;

import com.example.producer.config.SsoProperties;
import com.example.producer.sso.SamlService;
import com.example.producer.sso.SamlUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SamlController.class)
@EnableConfigurationProperties(SsoProperties.class)
@TestPropertySource(properties = "sso.frontend-url=http://localhost:5173/")
class SamlControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SamlService samlService;

    @Test
    @DisplayName("SAML 응답을 처리하면 프론트엔드 URL로 사용자 정보와 함께 리다이렉트한다")
    void redirectsToFrontendWithUserInfo() throws Exception {
        when(samlService.parseResponse("encoded-response"))
                .thenReturn(Optional.of(new SamlUser("hong@example.com", "홍길동", "hong@example.com")));

        mockMvc.perform(post("/saml/acs")
                        .param("SAMLResponse", "encoded-response"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:5173/?displayName=%ED%99%8D%EA%B8%B8%EB%8F%99&email=hong@example.com"));
    }

}
