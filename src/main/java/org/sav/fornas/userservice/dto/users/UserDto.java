package org.sav.fornas.userservice.dto.users;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
	private Long id;
	private String username;
	private String email;
	private String password;
	private boolean enabled = true;
	private String name;
	private String surname;
	private List<RoleDto> roles;
	private List<Long> rolesIds;
}
