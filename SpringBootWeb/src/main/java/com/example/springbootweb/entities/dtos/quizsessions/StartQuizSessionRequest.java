package com.example.springbootweb.entities.dtos.quizsessions;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO to start a new quiz session
 */
public record StartQuizSessionRequest(
    @NotNull(message = "Quiz ID is required")
    UUID quizId
) {}
