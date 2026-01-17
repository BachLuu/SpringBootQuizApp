package com.example.springbootweb.entities.dtos.analytics;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for Quiz Statistics
 * Contains pass/fail rates, question performance, and timing statistics
 */
public record QuizStatisticsResponse(
    UUID quizId,
    String quizTitle,
    
    // Basic Stats
    Long totalAttempts,
    Long completedAttempts,
    Long passedAttempts,
    Long failedAttempts,
    BigDecimal passRate,
    BigDecimal failRate,
    BigDecimal completionRate,
    
    // Score Statistics
    BigDecimal averageScore,
    BigDecimal highestScore,
    BigDecimal lowestScore,
    BigDecimal medianScore,
    
    // Time Statistics
    Integer averageTimeSeconds,
    Integer fastestTimeSeconds,
    Integer slowestTimeSeconds,
    
    // Question Performance
    List<QuestionPerformanceDto> questionPerformance,
    
    // Score Distribution
    List<ScoreDistributionDto> scoreDistribution
) {
    /**
     * Question performance statistics
     */
    public record QuestionPerformanceDto(
        UUID questionId,
        String questionContent,
        String questionType,
        Long totalAnswers,
        Long correctAnswers,
        Long incorrectAnswers,
        BigDecimal correctRate,
        Integer averageTimeSeconds,
        String difficultyLevel  // EASY, MEDIUM, HARD based on correct rate
    ) {}
    
    /**
     * Score distribution for histogram
     */
    public record ScoreDistributionDto(
        String range,     // "0-10", "11-20", etc.
        Long count,
        BigDecimal percentage
    ) {}
}
