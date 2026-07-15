package org.sav.fornas.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.dto.users.RoleDto;
import org.sav.fornas.userservice.dto.users.UserDto;
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

	public List<UserDto> getAllUsers(){
		List<UserEntity> usersEntity = userRepository.findAll();
		List<UserDto> usersDto = userMapper.toDtoList(usersEntity);
		log.debug(">>> usersDto:{}", usersDto);
		return usersDto;
	}

	public UserDto findById(Long id){
		UserEntity userEntity = userRepository.findById(id).orElseThrow();
		UserDto userDto = userMapper.toDto(userEntity);
		log.debug(">>> userDto:{}", userDto);
		return userDto;
	}

	public void saveUserProfile(UserDto editedUser, Long id){
		log.debug(">>> Profile editedUser:{}", editedUser);
		UserDto user = findById(id);
		user.setName(editedUser.getName());
		user.setSurname(editedUser.getSurname());
		if(editedUser.getPassword() != null && !editedUser.getPassword().isEmpty()){
			user.setPassword(passwordEncoder.encode(editedUser.getPassword()));
		}
		convertUserToEntityAndSave(user);
	}

	public void saveUserRoles(UserDto editedUser){
		log.debug(">>> Roles editedUser:{}", editedUser);
		UserDto user = findById(editedUser.getId());
		List<RoleEntity> roles = editedUser.getRolesIds() != null ? roleRepository.findAllById(editedUser.getRolesIds()) : null;
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
