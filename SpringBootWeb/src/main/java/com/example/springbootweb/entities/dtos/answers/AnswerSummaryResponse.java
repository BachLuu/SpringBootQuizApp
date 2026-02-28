package com.example.springbootweb.entities.dtos.answers;

import java.util.UUID;

public record AnswerSummaryResponse(
        String content,
        Boolean isCorrect,
        Boolean isActive,
        UUID questionId) {
}
