package com.example.springbootweb.entities.dtos.quizsessions;

import java.math.BigDecimal;

/**
 * Response DTO after submitting an answer
 */
public record SubmitAnswerResponse(
    Boolean success,
    Boolean isCorrect,
    BigDecimal pointsAwarded,
    Integer answeredQuestions,
    Integer totalQuestions,
    Integer remainingTimeSeconds,
    String message
) {}
