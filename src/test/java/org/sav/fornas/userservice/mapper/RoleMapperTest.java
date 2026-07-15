package org.sav.fornas.userservice.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.sav.fornas.userservice.dto.users.RoleDto;
import org.sav.fornas.userservice.entity.RoleEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleMapperTest {

	private final RoleMapper mapper = Mappers.getMapper(RoleMapper.class);

	@Test
	void shouldMapEntityToDto() {
		RoleEntity entity = new RoleEntity();
		entity.setId(1L);
		entity.setRoleName("ADMIN");
		entity.setDescription("Administrator role");

		RoleDto dto = mapper.toDto(entity);

		assertEquals(entity.getId(), dto.getId());
		assertEquals(entity.getRoleName(), dto.getRoleName());
		assertEquals(entity.getDescription(), dto.getDescription());
	}

	@Test
	void shouldMapDtoToEntity() {
		RoleDto dto = new RoleDto();
		dto.setId(1L);
		dto.setRoleName("USER");
		dto.setDescription("Regular user role");

		RoleEntity entity = mapper.toEntity(dto);

		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getRoleName(), entity.getRoleName());
		assertEquals(dto.getDescription(), entity.getDescription());
	}

	@Test
	void shouldMapEntityListToDtoList() {
		RoleEntity entity1 = new RoleEntity();
		entity1.setId(1L);
		entity1.setRoleName("ADMIN");

		RoleEntity entity2 = new RoleEntity();
		entity2.setId(2L);
		entity2.setRoleName("USER");

		List<RoleEntity> entities = List.of(entity1, entity2);
		List<RoleDto> dtos = mapper.toDtoList(entities);

		assertEquals(2, dtos.size());
		assertEquals(entity1.getId(), dtos.get(0).getId());
		assertEquals(entity2.getId(), dtos.get(1).getId());
	}

	@Test
	void shouldMapDtoListToEntityList() {
		RoleDto dto1 = new RoleDto();
		dto1.setId(1L);
		dto1.setRoleName("ADMIN");

		RoleDto dto2 = new RoleDto();
		dto2.setId(2L);
		dto2.setRoleName("USER");

		List<RoleDto> dtos = List.of(dto1, dto2);
		List<RoleEntity> entities = mapper.toEntityList(dtos);

		assertEquals(2, entities.size());
		assertEquals(dto1.getId(), entities.get(0).getId());
		assertEquals(dto2.getId(), entities.get(1).getId());
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