package com.example.springbootweb.entities.dtos.answers;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAnswerRequest(
        @NotBlank(message = "Content is required") @Size(min = 5, max = 5000, message = "Content must be between 5 and 5000 characters") String content,

        @NotNull(message = "IsCorrect is required") Boolean isCorrect,

        @NotNull(message = "Question ID is required") UUID questionId) {
}
