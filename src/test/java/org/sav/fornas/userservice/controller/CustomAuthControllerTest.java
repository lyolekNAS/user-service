package org.sav.fornas.userservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomAuthController.class)
class CustomAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(content().string(containsString("Login with OAuth 2.0")))
                .andExpect(content().string(containsString("/oauth2/authorization/google")));
    }

    @Test
    void logout_ShouldReturnLogoutView() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(content().string(containsString("Are you sure you want to log out?")))
                .andExpect(content().string(containsString("form-signin")));
    }
}