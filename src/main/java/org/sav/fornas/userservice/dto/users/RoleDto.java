package org.sav.fornas.userservice.dto.users;

import lombok.Data;

@Data
public class RoleDto {
	private Long id;
	private String roleName;
	private String description;
}
