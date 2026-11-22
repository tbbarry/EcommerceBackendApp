package com.backend.ecommerce.security;

import com.backend.ecommerce.controller.AdminContactMessageController;
import com.backend.ecommerce.controller.ContactMessageController;
import com.backend.ecommerce.service.ContactMessageService;
import com.backend.ecommerce.service.ContactRateLimiter;
import com.backend.ecommerce.service.email.EmailDeliveryException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ContactMessageController.class, AdminContactMessageController.class})
@Import({SecurityConfig.class, JwtFilter.class})
class ContactMessageRoutesTest {
    @Autowired MockMvc mvc;
    @MockBean ContactMessageService service;
    @MockBean ContactRateLimiter limiter;
    @MockBean JwtUtil jwt;
    @MockBean CustomUserDetailsService users;
    @MockBean(name = "corsConfigurationSource") CorsConfigurationSource cors;

    @Test
    void anonymousSubmissionIsValidatedAndHasNoPrivateResponse() throws Exception {
        when(limiter.allow(anyString())).thenReturn(true);
        mvc.perform(post("/api/contact-messages").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alex\",\"email\":\"alex@example.com\",\"message\":\"Hello\"}"))
                .andExpect(status().isCreated()).andExpect(content().string(""));
        verify(service).create(any());
        mvc.perform(post("/api/contact-messages").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\" \",\"email\":\"bad\",\"message\":\"hello\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/contact-messages").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"A\",\"email\":\"a@example.com\",\"message\":\"" + "x".repeat(4001) + "\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/contact-messages").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + "x".repeat(101) + "\",\"email\":\"a@example.com\",\"message\":\"hello\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/contact-messages").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + "x".repeat(100) + "\",\"email\":\"a@example.com\",\"message\":\"" + "x".repeat(4000) + "\"}"))
                .andExpect(status().isCreated());
        verify(service, times(2)).create(any());
        when(limiter.allow(anyString())).thenReturn(false);
        mvc.perform(post("/api/contact-messages").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"A\",\"email\":\"a@example.com\",\"message\":\"Hello\"}"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void anonymousCannotReadOrReply() throws Exception {
        mvc.perform(get("/api/admin/contact-messages")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/admin/contact-messages/1/reply")).andExpect(status().isUnauthorized());
    }

    @Test @WithMockUser(roles = "USER")
    void userCannotReadOrReply() throws Exception {
        mvc.perform(get("/api/admin/contact-messages")).andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/contact-messages/1/reply")).andExpect(status().isForbidden());
    }

    @Test @WithMockUser(roles = "ADMIN")
    void adminCanListAndReplyAndSubjectCannotInjectHeaders() throws Exception {
        when(service.list(0, 20)).thenReturn(Page.empty());
        mvc.perform(get("/api/admin/contact-messages")).andExpect(status().isOk());
        mvc.perform(post("/api/admin/contact-messages/1/reply").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subject\":\"Hello\",\"message\":\"Response\"}")).andExpect(status().isOk());
        mvc.perform(post("/api/admin/contact-messages/1/reply").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subject\":\"Hello\\nBcc: other@example.com\",\"message\":\"Response\"}"))
                .andExpect(status().isBadRequest());
        verify(service, times(1)).reply(eq(1), any());
    }

    @Test @WithMockUser(roles = "ADMIN")
    void replyErrorsHaveUsefulStatusWithoutMailInternals() throws Exception {
        when(service.reply(eq(1), any())).thenThrow(new EmailDeliveryException("secret recipient", null));
        mvc.perform(post("/api/admin/contact-messages/1/reply").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subject\":\"Hello\",\"message\":\"Response\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value("Email delivery failed. Please try again."));
        when(service.reply(eq(2), any())).thenThrow(new ContactMessageService.ContactAlreadyRepliedException());
        mvc.perform(post("/api/admin/contact-messages/2/reply").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subject\":\"Hello\",\"message\":\"Response\"}"))
                .andExpect(status().isConflict());
        when(service.reply(eq(3), any())).thenThrow(new ContactMessageService.ContactNotFoundException());
        mvc.perform(post("/api/admin/contact-messages/3/reply").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subject\":\"Hello\",\"message\":\"Response\"}"))
                .andExpect(status().isNotFound());
    }
}
