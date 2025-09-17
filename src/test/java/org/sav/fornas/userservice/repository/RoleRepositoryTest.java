package org.sav.fornas.userservice.repository;

import org.junit.jupiter.api.Test;
import org.sav.fornas.userservice.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByRoleName_ShouldReturnRole_WhenRoleExists() {
        // Given
        RoleEntity role = new RoleEntity();
        role.setRoleName("USER");
        role.setDescription("User role");
        entityManager.persistAndFlush(role);

        // When
        Optional<RoleEntity> result = roleRepository.findByRoleName("USER");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getRoleName()).isEqualTo("USER");
        assertThat(result.get().getDescription()).isEqualTo("User role");
    }

    @Test
    void findByRoleName_ShouldReturnEmpty_WhenRoleNotFound() {
        Optional<RoleEntity> result = roleRepository.findByRoleName("NONEXISTENT");

        assertThat(result).isEmpty();
    }

    @Test
    void findByRoleName_ShouldReturnCorrectRole_WhenMultipleRolesExist() {
        // Given
        RoleEntity userRole = new RoleEntity();
        userRole.setRoleName("USER");
        userRole.setDescription("User role");
        entityManager.persistAndFlush(userRole);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setRoleName("ADMIN");
        adminRole.setDescription("Admin role");
        entityManager.persistAndFlush(adminRole);

        // When
        Optional<RoleEntity> result = roleRepository.findByRoleName("ADMIN");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getRoleName()).isEqualTo("ADMIN");
        assertThat(result.get().getDescription()).isEqualTo("Admin role");
    }
}