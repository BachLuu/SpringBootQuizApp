package com.example.springbootweb.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.springbootweb.entities.dtos.roles.CreateRoleRequest;
import com.example.springbootweb.entities.dtos.roles.RoleDetailResponse;
import com.example.springbootweb.entities.dtos.roles.RoleSummaryResponse;
import com.example.springbootweb.entities.dtos.roles.UpdateRoleRequest;
import com.example.springbootweb.entities.models.Role;

@Mapper(config = CommonMapperConfig.class)
public interface RoleMapper {

    @Mapping(target = "id", source = "id")
    RoleDetailResponse toResponse(Role role);

    RoleSummaryResponse toSummary(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Role toEntity(CreateRoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(UpdateRoleRequest request, @MappingTarget Role role);
}
