package org.sav.fornas.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sav.fornas.userservice.dto.users.RoleDto;
import org.sav.fornas.userservice.dto.users.UserDto;
import org.sav.fornas.userservice.entity.RoleEntity;
import org.sav.fornas.userservice.entity.UserEntity;
import org.sav.fornas.userservice.mapper.RoleMapper;
import org.sav.fornas.userservice.mapper.UserMapper;
import org.sav.fornas.userservice.repository.RoleRepository;
import org.sav.fornas.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity testUserEntity;
    private UserDto testUserDto;
    private RoleEntity testRoleEntity;
    private RoleDto testRoleDto;

    @BeforeEach
    void setUp() {
        testUserEntity = new UserEntity();
        testUserEntity.setId(1L);
        testUserEntity.setUsername("testuser");
        testUserEntity.setEmail("test@example.com");
        testUserEntity.setName("John");
        testUserEntity.setSurname("Doe");

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setName("John");
        testUserDto.setSurname("Doe");

        testRoleEntity = new RoleEntity();
        testRoleEntity.setId(1L);
        testRoleEntity.setRoleName("USER");

        testRoleDto = new RoleDto();
        testRoleDto.setId(1L);
        testRoleDto.setRoleName("USER");
    }

    @Test
    void getAllUsers_ReturnsUserDtoList() {
        List<UserEntity> userEntities = List.of(testUserEntity);
        List<UserDto> userDtos = List.of(testUserDto);

        when(userRepository.findAll()).thenReturn(userEntities);
        when(userMapper.toDtoList(userEntities)).thenReturn(userDtos);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDto.getUsername(), result.get(0).getUsername());
        verify(userRepository).findAll();
        verify(userMapper).toDtoList(userEntities);
    }

    @Test
    void findById_ExistingUser_ReturnsUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);

        UserDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(testUserDto.getUsername(), result.getUsername());
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void saveUserProfile_WithPassword_UpdatesUserWithEncodedPassword() {
        UserDto editedUser = new UserDto();
        editedUser.setName("Jane");
        editedUser.setSurname("Smith");
        editedUser.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedpassword");
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUserEntity);

        userService.saveUserProfile(editedUser, 1L);

        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(testUserEntity);
    }

    @Test
    void saveUserProfile_WithoutPassword_UpdatesUserWithoutPasswordChange() {
        UserDto editedUser = new UserDto();
        editedUser.setName("Jane");
        editedUser.setSurname("Smith");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUserEntity);

        userService.saveUserProfile(editedUser, 1L);

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(testUserEntity);
    }

    @Test
    void saveUserRoles_UpdatesUserRoles() {
        UserDto editedUser = new UserDto();
        editedUser.setId(1L);
        editedUser.setRolesIds(List.of(1L));

        List<RoleEntity> roles = List.of(testRoleEntity);
        List<RoleDto> roleDtos = List.of(testRoleDto);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDto(testUserEntity)).thenReturn(testUserDto);
        when(roleRepository.findAllById(List.of(1L))).thenReturn(roles);
        when(roleMapper.toDtoList(roles)).thenReturn(roleDtos);
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUserEntity);

        userService.saveUserRoles(editedUser);

        verify(roleRepository).findAllById(List.of(1L));
        verify(userRepository).save(testUserEntity);
    }

    @Test
    void getAllRoles_ReturnsRoleDtoList() {
        List<RoleEntity> roleEntities = List.of(testRoleEntity);
        List<RoleDto> roleDtos = List.of(testRoleDto);

        when(roleRepository.findAll()).thenReturn(roleEntities);
        when(roleMapper.toDtoList(roleEntities)).thenReturn(roleDtos);

        List<RoleDto> result = userService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRoleDto.getRoleName(), result.get(0).getRoleName());
        verify(roleRepository).findAll();
        verify(roleMapper).toDtoList(roleEntities);
    }
}