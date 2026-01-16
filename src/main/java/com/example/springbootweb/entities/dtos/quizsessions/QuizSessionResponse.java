package com.example.springbootweb.entities.dtos.quizsessions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.springbootweb.entities.enums.QuizSessionStatus;

/**
 * Response DTO for quiz session summary
 */
public record QuizSessionResponse(
    UUID id,
    UUID quizId,
    String quizTitle,
    UUID userId,
    String userName,
    QuizSessionStatus status,
    LocalDateTime createdAt,
    LocalDateTime startedAt,
    LocalDateTime finishedAt,
    LocalDateTime expiresAt,
    Integer timeSpentSeconds,
    Integer totalQuestions,
    Integer answeredQuestions,
    Integer correctAnswers,
    BigDecimal score,
    BigDecimal pointsEarned,
    BigDecimal maxPoints,
    Boolean isPassed,
    Integer currentQuestionIndex,
    Integer remainingTimeSeconds
) {}
