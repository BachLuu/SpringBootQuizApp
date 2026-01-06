package com.example.springbootweb.services.interfaces;

import com.example.springbootweb.entities.dtos.users.CreateUserRequest;
import com.example.springbootweb.entities.dtos.users.UpdateUserRequest;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;
import com.example.springbootweb.entities.dtos.users.UserSummaryResponse;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    List<UserSummaryResponse> getAllUsers();

    Page<UserSummaryResponse> getPagedUsers(Integer page, Integer size);

    UserDetailResponse getUserById(UUID id);

    List<UserSummaryResponse> getActiveUsers();

    List<UserSummaryResponse> searchUsers(String keyword);

    UserDetailResponse createUser(CreateUserRequest createUserRequest);

    UserDetailResponse updateUser(UUID id, UpdateUserRequest updateUserRequest);

    void deleteUser(UUID id);

    long getTotalUsers();
}
