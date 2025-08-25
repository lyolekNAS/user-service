package org.sav.fornas.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.dto.users.UserDto;
import org.sav.fornas.userservice.entity.UserEntity;
import org.sav.fornas.userservice.mapper.UserMapper;
import org.sav.fornas.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public UserDto findByUsername(String userName){
		UserEntity userEntity = userRepository.findByUsername(userName).orElseThrow();
//		log.debug(">>> userEntity:{} {}", userEntity, userEntity.getRoles());
		log.debug(">>> userEntity:{}", userEntity);
		UserDto userDto = userMapper.toDto(userEntity);
		log.debug(">>> userDto:{}", userDto);
		return userDto;
	}

	public void saveUser(UserDto editedUser, String userName){
		log.debug(">>> editedUser:{}", editedUser);
		UserDto user = findByUsername(userName);
		user.setName(editedUser.getName());
		user.setSurname(editedUser.getSurname());
		if(editedUser.getPassword() != null && !editedUser.getPassword().isEmpty()){
			user.setPassword(passwordEncoder.encode(editedUser.getPassword()));
		}
		log.debug(">>> user:{}", user);
		UserEntity userEntity = userMapper.toEntity(user);
		log.debug(">>> userEntity:{}", userEntity);
		userRepository.save(userEntity);
	}
}
