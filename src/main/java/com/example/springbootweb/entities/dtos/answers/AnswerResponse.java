package com.example.springbootweb.entities.dtos.answers;

import java.util.UUID;

public record AnswerResponse(
        UUID id,
        String content,
        Boolean isCorrect,
        Boolean isActive,
        UUID questionId) {
}
