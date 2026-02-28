package com.example.springbootweb.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.springbootweb.entities.dtos.roles.RoleSummaryResponse;
import com.example.springbootweb.entities.dtos.users.CreateUserRequest;
import com.example.springbootweb.entities.dtos.users.UpdateUserRequest;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;
import com.example.springbootweb.entities.dtos.users.UserSummaryResponse;
import com.example.springbootweb.entities.models.Role;
import com.example.springbootweb.entities.models.User;

@Mapper(config = CommonMapperConfig.class)
public interface UserMapper {

	@Mapping(target = "roles", expression = "java(mapToRoleSummaryResponses(user.getRoles()))")
	UserDetailResponse toResponse(User user);

	@Mapping(target = "roles", expression = "java(mapRoleNames(user.getRoles()))")
	UserSummaryResponse toSummary(User user);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userQuizzes", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "isActive", expression = "java(Boolean.TRUE)")
	User toEntity(CreateUserRequest request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userQuizzes", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "email", ignore = true)
	void updateEntity(UpdateUserRequest request, @MappingTarget User user);

	default Set<RoleSummaryResponse> mapToRoleSummaryResponses(Set<Role> roles) {
		return roles == null ? Set.of()
				: roles.stream()
					.map(role -> new RoleSummaryResponse(role.getId(), role.getName(), role.getIsActive()))
					.collect(Collectors.toSet());
	}

	default Set<String> mapRoleNames(Set<Role> roles) {
		return roles == null ? Set.of() : roles.stream().map(Role::getName).collect(Collectors.toSet());
	}

}
