package com.example.springbootweb.services.impl;

import com.example.springbootweb.entities.constants.ErrorMessage;
import com.example.springbootweb.entities.dtos.users.CreateUserDto;
import com.example.springbootweb.entities.dtos.users.UpdateUserDto;
import com.example.springbootweb.entities.dtos.users.UserResponseDto;
import com.example.springbootweb.entities.models.Role;
import com.example.springbootweb.entities.models.User;
import com.example.springbootweb.exceptions.BadRequestException;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.repositories.RoleRepository;
import com.example.springbootweb.repositories.UserRepository;
import com.example.springbootweb.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getPagedUsers(Integer page, Integer size) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;

        if (pageNumber < 0) {
            throw new BadRequestException("Page must be >= 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        Page<User> userPage = userRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return userPage.map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        logger.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND + ": " + id));
        return mapToResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getActiveUsers() {
        logger.info("Fetching active users");
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsers(String keyword) {
        logger.info("Searching users with keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("Search keyword cannot be empty");
        }
        List<User> users = userRepository
                .findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword,
                        keyword, keyword);
        return users.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserDto createUserDto) {
        logger.info("Creating new user: {}", createUserDto.getEmail());
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new BadRequestException(ErrorMessage.EMAIL_ALREADY_EXISTS + ": " + createUserDto.getEmail());
        }

        Set<Role> roles = new HashSet<>();
        if (createUserDto.getRoleIds() != null && !createUserDto.getRoleIds().isEmpty()) {
            roles.addAll(roleRepository.findAllById(createUserDto.getRoleIds()));
        }

        User user = User.builder()
                .firstName(createUserDto.getFirstName())
                .lastName(createUserDto.getLastName())
                .email(createUserDto.getEmail())
                .password(passwordEncoder.encode(createUserDto.getPassword()))
                .avatar(createUserDto.getAvatar())
                .dateOfBirth(createUserDto.getDateOfBirth())
                .isActive(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UUID id, UpdateUserDto updateUserDto) {
        logger.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND + ": " + id));

        if (updateUserDto.getFirstName() != null) {
            user.setFirstName(updateUserDto.getFirstName());
        }
        if (updateUserDto.getLastName() != null) {
            user.setLastName(updateUserDto.getLastName());
        }
        if (updateUserDto.getAvatar() != null) {
            user.setAvatar(updateUserDto.getAvatar());
        }
        if (updateUserDto.getDateOfBirth() != null) {
            user.setDateOfBirth(updateUserDto.getDateOfBirth());
        }
        if (updateUserDto.getIsActive() != null) {
            user.setIsActive(updateUserDto.getIsActive());
        }
        if (updateUserDto.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(updateUserDto.getRoleIds()));
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        logger.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND + ": " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUsers() {
        return userRepository.count();
    }

    private UserResponseDto mapToResponseDto(User user) {
        Set<UserResponseDto.RoleDto> roleDtos = user.getRoles().stream()
                .map(role -> UserResponseDto.RoleDto.builder()
                        .name(role.getName())
                        .isActive(role.getIsActive())
                        .build())
                .collect(Collectors.toSet());

        return UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .dateOfBirth(user.getDateOfBirth())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .roles(roleDtos)
                .displayName(user.getDisplayName())
                .build();
    }
}
