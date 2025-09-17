package org.sav.fornas.userservice.repository;

import org.junit.jupiter.api.Test;
import org.sav.fornas.userservice.entity.RoleEntity;
import org.sav.fornas.userservice.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_ShouldReturnUserWithRoles() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setRoleName("USER");
        role.setDescription("User role");
        entityManager.persistAndFlush(role);

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("Test");
        user.setSurname("User");
        user.setRoles(List.of(role));
        entityManager.persistAndFlush(user);

        // When
        Optional<UserEntity> result = userRepository.findByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getRoles()).hasSize(1);
        assertThat(result.get().getRoles().get(0).getRoleName()).isEqualTo("USER");
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUserNotFound() {
        // When
        Optional<UserEntity> result = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_ShouldReturnUserWithRoles() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setRoleName("ADMIN");
        role.setDescription("Admin role");
        entityManager.persistAndFlush(role);

        UserEntity user = new UserEntity();
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setPassword("password");
        user.setName("Admin");
        user.setSurname("User");
        user.setRoles(List.of(role));
        entityManager.persistAndFlush(user);

        // When
        Optional<UserEntity> result = userRepository.findByEmail("admin@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("admin@example.com");
        assertThat(result.get().getRoles()).hasSize(1);
        assertThat(result.get().getRoles().get(0).getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserNotFound() {
        // When
        Optional<UserEntity> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }
}