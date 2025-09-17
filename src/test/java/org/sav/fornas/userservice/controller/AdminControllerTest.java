package org.sav.fornas.userservice.controller;

import org.junit.jupiter.api.Test;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", roles = {"CARDS_ADMIN"})
    void getUsers_ShouldReturnUsersView() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/users"))
                .andExpect(model().attributeExists("users"));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CARDS_ADMIN"})
    void getUserRoles_ShouldReturnRolesView() throws Exception {
        UserDto user = new UserDto();
        when(userService.findById(1L)).thenReturn(user);
        when(userService.getAllRoles()).thenReturn(List.of());

        mockMvc.perform(get("/admin/roles").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/roles"))
                .andExpect(model().attributeExists("user", "roles"));

        verify(userService).findById(1L);
        verify(userService).getAllRoles();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"CARDS_ADMIN"})
    void setUserRoles_ShouldRedirectToRoles() throws Exception {
        mockMvc.perform(
                    post("/admin/roles")
                    .param("id", "1")
                    .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/roles?id=1"));

        verify(userService).saveUserRoles(any(UserDto.class));
    }
}