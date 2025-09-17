package org.sav.fornas.userservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.sav.fornas.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = CustomUserDetails.builder()
                .id(1L)
                .username("admin")
                .build();

    }

    @Test
    void home_ShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void profile_ShouldReturnProfileViewWithUser() throws Exception {
        UserDto userDto = new UserDto();

        when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/profile")
                        .with(user(userDetails))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("user", userDto));

        verify(userService).findById(1L);
    }

    @Test
    void editProfile_ShouldSaveAndRedirect() throws Exception {
        ArgumentCaptor<UserDto> captor = ArgumentCaptor.forClass(UserDto.class);

        mockMvc.perform(
                    post("/profile")
                            .param("name", "Test User")
                            .param("surname", "Tester")
                            .with(user(userDetails))
                            .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(userService).saveUserProfile(captor.capture(), eq(1L));

        UserDto saved = captor.getValue();
        assertEquals("Test User", saved.getName());
        assertEquals("Tester", saved.getSurname());
    }
}