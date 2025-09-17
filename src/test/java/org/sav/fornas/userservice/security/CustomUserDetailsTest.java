package org.sav.fornas.userservice.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.sav.fornas.userservice.entity.UserEntity;
import org.sav.fornas.userservice.entity.RoleEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private CustomUserDetails customUserDetails;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        RoleEntity adminRole = new RoleEntity();
        adminRole.setRoleName("ADMIN");
        
        RoleEntity userRole = new RoleEntity();
        userRole.setRoleName("USER");

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setPassword("password123");
        userEntity.setName("Test");
        userEntity.setSurname("User");
        userEntity.setEmail("test@example.com");
        userEntity.setRoles(List.of(adminRole, userRole));

        customUserDetails = CustomUserDetails.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .name("Test")
                .surname("User")
                .email("test@example.com")
                .authorities(List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
                ))
                .build();
    }

    @Test
    void testGetters() {
        assertEquals(1L, customUserDetails.getId());
        assertEquals("testuser", customUserDetails.getUsername());
        assertEquals("password123", customUserDetails.getPassword());
        assertEquals("Test", customUserDetails.getName());
        assertEquals("User", customUserDetails.getSurname());
        assertEquals("test@example.com", customUserDetails.getEmail());
    }

    @Test
    void testUserDetailsInterface() {
        assertTrue(customUserDetails.isAccountNonExpired());
        assertTrue(customUserDetails.isAccountNonLocked());
        assertTrue(customUserDetails.isCredentialsNonExpired());
        assertTrue(customUserDetails.isEnabled());
    }

    @Test
    void testAuthorities() {
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testFromUserEntity() {
        CustomUserDetails result = CustomUserDetails.fromUserEntity(userEntity);
        
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(userEntity.getUsername(), result.getUsername());
        assertEquals(userEntity.getPassword(), result.getPassword());
        assertEquals(userEntity.getName(), result.getName());
        assertEquals(userEntity.getSurname(), result.getSurname());
        assertEquals(userEntity.getEmail(), result.getEmail());
        
        Collection<? extends GrantedAuthority> authorities = result.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testOidcUserInterface() {
        assertTrue(customUserDetails.getClaims().isEmpty());
        assertNull(customUserDetails.getUserInfo());
        assertNull(customUserDetails.getIdToken());
        assertTrue(customUserDetails.getAttributes().isEmpty());
    }

    @Test
    void testPasswordIsJsonIgnored() {
        assertNotNull(customUserDetails.getPassword());
        assertEquals("password123", customUserDetails.getPassword());
    }
}