package com.example.springbootweb.entities.dtos.answers;

import java.util.UUID;

import org.jspecify.annotations.NonNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAnswerDto {
    @NotBlank(message = "Content is required")
    @Size(min = 5, max = 5000, message = "Content must be between 5 and 5000 characters")
    private String content;

    @NonNull
    @NotNull(message = "IsCorrect is required")
    private Boolean isCorrect;

    @NonNull
    @NotNull(message = "Question ID is required")
    private UUID questionId;
}
