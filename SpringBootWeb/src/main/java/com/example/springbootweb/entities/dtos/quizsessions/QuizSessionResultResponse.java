package com.example.springbootweb.entities.dtos.quizsessions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.springbootweb.entities.enums.QuizSessionStatus;

/**
 * Detailed response DTO for quiz session result
 */
public record QuizSessionResultResponse(
    UUID id,
    UUID quizId,
    String quizTitle,
    String quizDescription,
    UUID userId,
    String userName,
    QuizSessionStatus status,
    LocalDateTime startedAt,
    LocalDateTime finishedAt,
    Integer timeSpentSeconds,
    Integer totalQuestions,
    Integer answeredQuestions,
    Integer correctAnswers,
    Integer incorrectAnswers,
    Integer skippedQuestions,
    BigDecimal score,
    BigDecimal pointsEarned,
    BigDecimal maxPoints,
    Boolean isPassed,
    BigDecimal passingScore,
    List<AnswerResultResponse> answerResults,
    QuizStatistics statistics
) {
    /**
     * Individual answer result
     */
    public record AnswerResultResponse(
        UUID questionId,
        String questionContent,
        String questionType,
        UUID selectedAnswerId,
        String selectedAnswerContent,
        UUID correctAnswerId,
        String correctAnswerContent,
        String textResponse,
        Boolean isCorrect,
        BigDecimal pointsAwarded,
        BigDecimal maxPoints,
        Integer timeSpentSeconds,
        Boolean isReviewed,
        String reviewerFeedback,
        List<AnswerOption> allOptions
    ) {}

    /**
     * Answer option for showing all choices
     */
    public record AnswerOption(
        UUID id,
        String content,
        Boolean isCorrect,
        Boolean wasSelected
    ) {}

    /**
     * Quiz completion statistics
     */
    public record QuizStatistics(
        Double accuracyPercentage,
        Integer averageTimePerQuestion,
        String fastestAnswer,
        String slowestAnswer,
        Integer rank,
        Integer totalParticipants
    ) {}
}
