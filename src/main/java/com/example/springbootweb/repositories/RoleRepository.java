package com.example.springbootweb.repositories;

import com.example.springbootweb.entities.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {
    List<Role> findByIsActiveTrue();

    List<Role> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);

    Optional<Role> findByName(String name);
}
