package com.example.springbootweb.services.interfaces;

import com.example.springbootweb.entities.dtos.users.CreateUserDto;
import com.example.springbootweb.entities.dtos.users.UpdateUserDto;
import com.example.springbootweb.entities.dtos.users.UserResponseDto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    List<UserResponseDto> getAllUsers();

    Page<UserResponseDto> getPagedUsers(Integer page, Integer size);

    UserResponseDto getUserById(UUID id);

    List<UserResponseDto> getActiveUsers();

    List<UserResponseDto> searchUsers(String keyword);

    UserResponseDto createUser(CreateUserDto createUserDto);

    UserResponseDto updateUser(UUID id, UpdateUserDto updateUserDto);

    void deleteUser(UUID id);

    long getTotalUsers();
}
