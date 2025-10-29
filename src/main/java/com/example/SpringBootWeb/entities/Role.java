package com.example.SpringBootWeb.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

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

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}