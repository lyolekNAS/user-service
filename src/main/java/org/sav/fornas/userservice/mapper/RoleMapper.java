package org.sav.fornas.userservice.mapper;

import org.mapstruct.Mapper;
import org.sav.fornas.dto.users.RoleDto;
import org.sav.fornas.userservice.entity.RoleEntity;

@Mapper(componentModel = "spring")
public interface RoleMapper {

	RoleDto toDto(RoleEntity role);
	RoleEntity toEntity(RoleDto role);
}
