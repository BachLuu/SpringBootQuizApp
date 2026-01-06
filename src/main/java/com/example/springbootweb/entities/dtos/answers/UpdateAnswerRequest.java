package com.example.springbootweb.entities.dtos.answers;

import java.util.UUID;

import jakarta.validation.constraints.Size;

public record UpdateAnswerRequest(
        @Size(min = 5, max = 5000, message = "Content must be between 5 and 5000 characters") String content,
        Boolean isCorrect,
        Boolean isActive,
        UUID questionId) {
}
