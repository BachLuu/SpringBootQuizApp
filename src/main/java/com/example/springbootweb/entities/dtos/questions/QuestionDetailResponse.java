package com.example.springbootweb.entities.dtos.questions;

import java.util.UUID;

import com.example.springbootweb.entities.enums.QuestionType;

public record QuestionDetailResponse(UUID id, String content, QuestionType questionType, Boolean isActive) {
}
