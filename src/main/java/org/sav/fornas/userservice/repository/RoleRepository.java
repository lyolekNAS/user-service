package org.sav.fornas.userservice.repository;

import org.sav.fornas.userservice.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
	Optional<RoleEntity> findByRoleName(String roleName);
}
