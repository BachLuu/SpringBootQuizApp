package com.example.SpringBootWeb.services.interfaces;

import com.example.SpringBootWeb.entities.dtos.users.CreateUserDto;
import com.example.SpringBootWeb.entities.dtos.users.UpdateUserDto;
import com.example.SpringBootWeb.entities.dtos.users.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(UUID id);

    List<UserResponseDto> getActiveUsers();

    List<UserResponseDto> searchUsers(String keyword);

    UserResponseDto createUser(CreateUserDto createUserDto);

    UserResponseDto updateUser(UUID id, UpdateUserDto updateUserDto);

    void deleteUser(UUID id);

    long getTotalUsers();
}
