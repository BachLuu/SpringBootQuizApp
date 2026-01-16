package com.example.springbootweb.entities.dtos.quizsessions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.springbootweb.entities.enums.QuizSessionStatus;

/**
 * Summary response for quiz session list (user history)
 */
public record QuizSessionSummaryResponse(
    UUID id,
    UUID quizId,
    String quizTitle,
    String quizThumbnail,
    QuizSessionStatus status,
    LocalDateTime startedAt,
    LocalDateTime finishedAt,
    Integer timeSpentSeconds,
    Integer totalQuestions,
    Integer correctAnswers,
    BigDecimal score,
    Boolean isPassed
) {}
