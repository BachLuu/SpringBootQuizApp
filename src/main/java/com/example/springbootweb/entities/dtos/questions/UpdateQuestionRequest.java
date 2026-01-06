package com.example.springbootweb.entities.dtos.questions;

import com.example.springbootweb.entities.enums.QuestionType;

import jakarta.validation.constraints.Size;

public record UpdateQuestionRequest(
        @Size(min = 5, max = 5000, message = "Content must be between 5 and 5000 characters")
        String content,
        QuestionType questionType,
        Boolean isActive) {
}
