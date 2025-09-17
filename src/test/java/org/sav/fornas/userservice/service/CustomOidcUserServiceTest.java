package org.sav.fornas.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sav.fornas.userservice.entity.UserEntity;
import org.sav.fornas.userservice.repository.UserRepository;
import org.sav.fornas.userservice.security.CustomUserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOidcUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OidcUserRequest oidcUserRequest;

    @Mock
    private OidcUser oidcUser;

    @Mock
    private OidcUserService oidcUserService; // без new !!!

    @InjectMocks
    private CustomOidcUserService customOidcUserService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("test@example.com");
        testUser.setName("John");
        testUser.setSurname("Doe");

        customOidcUserService.setOidcUserService(oidcUserService);
        when(oidcUserService.loadUser(any(OidcUserRequest.class))).thenReturn(oidcUser);
    }

    @Test
    void loadUser_ExistingUser_ReturnsCustomUserDetails() {
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        OidcUser result = customOidcUserService.loadUser(oidcUserRequest);

        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void loadUser_NewUser_CreatesAndSavesUser() {
        when(oidcUser.getEmail()).thenReturn("new@example.com");
        when(oidcUser.getAttribute("given_name")).thenReturn("Jane");
        when(oidcUser.getAttribute("family_name")).thenReturn("Smith");
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        OidcUser result = customOidcUserService.loadUser(oidcUserRequest);

        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).save(any(UserEntity.class));
    }
}
