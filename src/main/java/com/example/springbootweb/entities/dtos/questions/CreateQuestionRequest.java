package com.example.springbootweb.entities.dtos.questions;

import com.example.springbootweb.entities.enums.QuestionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateQuestionRequest(
        @NotBlank(message = "Content is required") @Size(min = 5, max = 5000, message = "Content must be between 5 and 5000 characters") String content,

        @NotNull(message = "Question type is required") QuestionType questionType) {
}
