package org.sav.fornas.userservice.mapper;

import org.mapstruct.Mapper;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDto toDto(UserEntity user);
	UserEntity toEntity(UserDto userDto);

}
