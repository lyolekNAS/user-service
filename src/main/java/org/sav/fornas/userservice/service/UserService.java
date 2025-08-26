package org.sav.fornas.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.dto.users.RoleDto;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.entity.RoleEntity;
import org.sav.fornas.userservice.entity.UserEntity;
import org.sav.fornas.userservice.mapper.RoleMapper;
import org.sav.fornas.userservice.mapper.UserMapper;
import org.sav.fornas.userservice.repository.RoleRepository;
import org.sav.fornas.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final RoleMapper roleMapper;
	private final PasswordEncoder passwordEncoder;

	public UserDto findByUsername(String userName){
		UserEntity userEntity = userRepository.findByUsername(userName).orElseThrow();
		UserDto userDto = userMapper.toDto(userEntity);
		log.debug(">>> userDto:{}", userDto);
		return userDto;
	}

	public void saveUserProfile(UserDto editedUser, String userName){
		log.debug(">>> Profile editedUser:{}", editedUser);
		UserDto user = findByUsername(userName);
		user.setName(editedUser.getName());
		user.setSurname(editedUser.getSurname());
		if(editedUser.getPassword() != null && !editedUser.getPassword().isEmpty()){
			user.setPassword(passwordEncoder.encode(editedUser.getPassword()));
		}
		convertUserToEntityAndSave(user);
	}

	public void saveUserRoles(UserDto editedUser){
		log.debug(">>> Roles editedUser:{}", editedUser);
		UserDto user = findByUsername(editedUser.getUsername());
		List<RoleEntity> roles = roleRepository.findAllById(editedUser.getRolesIds());
		user.setRoles((roleMapper.toDtoList(roles)));
		convertUserToEntityAndSave(user);
	}

	public List<RoleDto> getAllRoles() {
		return roleMapper.toDtoList(roleRepository.findAll());
	}

	private void convertUserToEntityAndSave(UserDto userDto) {
		log.debug(">>> user:{}", userDto);
		UserEntity userEntity = userMapper.toEntity(userDto);
		log.debug(">>> userEntity:{}", userEntity);
		userRepository.save(userEntity);
	}
}
