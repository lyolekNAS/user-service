package org.sav.fornas.userservice.service;

import lombok.RequiredArgsConstructor;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.entity.UserEntity;
import org.sav.fornas.userservice.mapper.UserMapper;
import org.sav.fornas.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public UserDto findByUsername(String userName){
		return userMapper.toDto(userRepository.findByUsername(userName).orElseThrow());
	}

	public UserEntity saveUser(UserDto editedUser, String userName){
		UserDto user = findByUsername(userName);
		user.setName(editedUser.getName());
		user.setSurname(editedUser.getSurname());
		return userRepository.findByUsername(userName).orElseThrow();
	}
}
