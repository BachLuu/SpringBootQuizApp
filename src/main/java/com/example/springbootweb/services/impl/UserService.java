package com.example.springbootweb.services.impl;

import com.example.springbootweb.entities.constants.ErrorMessage;
import com.example.springbootweb.entities.dtos.users.CreateUserRequest;
import com.example.springbootweb.entities.dtos.users.UpdateUserRequest;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;
import com.example.springbootweb.entities.dtos.users.UserFilter;
import com.example.springbootweb.entities.dtos.users.UserSummaryResponse;
import com.example.springbootweb.entities.models.Role;
import com.example.springbootweb.entities.models.User;
import com.example.springbootweb.exceptions.BadRequestException;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.mappers.UserMapper;
import com.example.springbootweb.repositories.RoleRepository;
import com.example.springbootweb.repositories.UserRepository;
import com.example.springbootweb.repositories.specifications.UserSpecifications;
import com.example.springbootweb.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers(UserFilter filter) {
        logger.info("Fetching all users with filter: {}", filter);
        Specification<User> spec = UserSpecifications.fromFilter(filter);
        List<User> users = userRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "email"));
        return users.stream()
                .map(userMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> getPagedUsers(Integer page, Integer size, UserFilter filter) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;

        logger.info("Fetching paged users - page: {}, size: {}, filter: {}", pageNumber, pageSize, filter);

        if (pageNumber < 0) {
            throw new BadRequestException("Page must be >= 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        Specification<User> spec = UserSpecifications.fromFilter(filter);
        Page<User> userPage = userRepository.findAll(spec, PageRequest.of(pageNumber, pageSize));
        return userPage.map(userMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(UUID id) {
        logger.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND + ": " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getActiveUsers() {
        logger.info("Fetching active users");
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream()
                .map(userMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> searchUsers(String keyword) {
        logger.info("Searching users with keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("Search keyword cannot be empty");
        }
        List<User> users = userRepository
                .findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword,
                        keyword, keyword);
        return users.stream()
                .map(userMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDetailResponse createUser(CreateUserRequest createUserRequest) {
        logger.info("Creating new user: {}", createUserRequest.email());
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new BadRequestException(ErrorMessage.EMAIL_ALREADY_EXISTS + ": " + createUserRequest.email());
        }

        Set<Role> roles = new HashSet<>();
        if (createUserRequest.roleIds() != null && !createUserRequest.roleIds().isEmpty()) {
            roles.addAll(roleRepository.findAllById(createUserRequest.roleIds()));
        }

        User user = userMapper.toEntity(createUserRequest);
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserDetailResponse updateUser(UUID id, UpdateUserRequest updateUserRequest) {
        logger.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND + ": " + id));

        if (updateUserRequest.roleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(updateUserRequest.roleIds()));
            user.setRoles(roles);
        }

        userMapper.updateEntity(updateUserRequest, user);

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
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

}
