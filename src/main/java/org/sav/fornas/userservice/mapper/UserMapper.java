package org.sav.fornas.userservice.mapper;

import org.mapstruct.Mapper;
import org.sav.fornas.userservice.dto.users.UserDto;
import org.sav.fornas.userservice.entity.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDto toDto(UserEntity user);
	UserEntity toEntity(UserDto userDto);

	List<UserDto> toDtoList(List<UserEntity> users);
}
