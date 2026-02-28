package com.example.springbootweb.entities.models;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.NonNull;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @Id
    @UuidGenerator
    private UUID id;

    @Basic(fetch = FetchType.LAZY)
    @NonNull
    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Basic(fetch = FetchType.LAZY)
    @NonNull
    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}