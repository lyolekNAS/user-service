package org.sav.fornas.userservice.mapper;

import org.mapstruct.Mapper;
import org.sav.fornas.userservice.dto.users.RoleDto;
import org.sav.fornas.userservice.entity.RoleEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

	RoleDto toDto(RoleEntity role);
	RoleEntity toEntity(RoleDto role);

	List<RoleDto> toDtoList(List<RoleEntity> roles);
	List<RoleEntity> toEntityList(List<RoleDto> roles);
}
