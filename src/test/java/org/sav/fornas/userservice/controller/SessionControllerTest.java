package org.sav.fornas.userservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.sav.fornas.dto.sessions.SessionDto;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.sav.fornas.userservice.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @MockBean
    private SessionService sessionService;

    @InjectMocks
    private SessionController sessionController;

    @Autowired
    private MockMvc mockMvc;

    private CustomUserDetails userDetails;

    private final String appName = "user-service";

    @BeforeEach
    void setUp() {
        userDetails = CustomUserDetails.builder()
                .id(1L)
                .username("admin")
                .build();

    }

    @Test
    void getAllSessions_ShouldReturnSessionsView() throws Exception {
        List<SessionDto> sessions = List.of(new SessionDto("1"), new SessionDto("2"));
        when(sessionService.getAllSessions(appName)).thenReturn(sessions);

        mockMvc.perform(get("/session/all/" + appName).with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("/session/all"))
                .andExpect(model().attribute("sessions", sessions));

        verify(sessionService).getAllSessions(appName);
    }

    @Test
    void deleteSession_ShouldDeleteAndRedirect() throws Exception {
        mockMvc.perform(get("/session/delete/" + appName).param("sid", "test-session-id").with(user(userDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/session/all/" + appName));

        verify(sessionService).deleteSession("test-session-id", appName);
    }
}