package com.example.springbootweb.entities.dtos.quizzes;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateQuizRequest(
        @NotBlank(message = "Title is required") @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters") String title,

        @NotBlank(message = "Description is required") @Size(max = 1000, message = "Description must not exceed 1000 characters") String description,

        @NotNull(message = "Duration is required") @Min(value = 1, message = "Duration must be at least 1 minute") @Max(value = 3600, message = "Duration must not exceed 3600 minutes") Integer duration,

        @Size(max = 500, message = "Thumbnail URL must not exceed 500 characters") String thumbnailUrl,

        Boolean isActive) {
}
