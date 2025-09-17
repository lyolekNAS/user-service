package org.sav.fornas.userservice.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.entity.UserEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

	private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

	@Test
	void shouldMapEntityToDto() {
		UserEntity entity = new UserEntity();
		entity.setId(1L);
		entity.setUsername("testuser");
		entity.setEmail("test@example.com");
		entity.setPassword("password");
		entity.setEnabled(true);
		entity.setName("John");
		entity.setSurname("Doe");

		UserDto dto = mapper.toDto(entity);

		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getUsername(), dto.getUsername());
		assertEquals(entity.getEmail(), dto.getEmail());
		assertEquals(entity.isEnabled(), dto.isEnabled());
		assertEquals(entity.getName(), dto.getName());
		assertEquals(entity.getSurname(), dto.getSurname());
	}

	@Test
	void shouldMapDtoToEntity() {
		UserDto dto = new UserDto();
		dto.setId(1L);
		dto.setUsername("testuser");
		dto.setEmail("test@example.com");
		dto.setEnabled(true);
		dto.setName("John");
		dto.setSurname("Doe");

		UserEntity entity = mapper.toEntity(dto);

		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getUsername(), entity.getUsername());
		assertEquals(dto.getEmail(), entity.getEmail());
		assertEquals(dto.isEnabled(), entity.isEnabled());
		assertEquals(dto.getName(), entity.getName());
		assertEquals(dto.getSurname(), entity.getSurname());
	}

	@Test
	void shouldMapEntityListToDtoList() {
		UserEntity entity1 = new UserEntity();
		entity1.setId(1L);
		entity1.setUsername("user1");

		UserEntity entity2 = new UserEntity();
		entity2.setId(2L);
		entity2.setUsername("user2");

		List<UserEntity> entities = List.of(entity1, entity2);
		List<UserDto> dtos = mapper.toDtoList(entities);

		assertEquals(2, dtos.size());
		assertEquals(entity1.getId(), dtos.get(0).getId());
		assertEquals(entity2.getId(), dtos.get(1).getId());
	}

	@Test
	void shouldReturnNullWhenEntityIsNull() {
		assertNull(mapper.toDto(null));
	}

	@Test
	void shouldReturnNullWhenDtoIsNull() {
		assertNull(mapper.toEntity(null));
	}
}