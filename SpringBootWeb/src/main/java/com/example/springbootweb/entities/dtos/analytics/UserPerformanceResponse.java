package com.example.springbootweb.entities.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for User Performance Dashboard
 * Contains quiz history, statistics, and strengths/weaknesses
 */
public record UserPerformanceResponse(
    UUID userId,
    String userName,
    String userEmail,
    
    // Overview Statistics
    OverviewStats overview,
    
    // Performance by Question Type
    List<QuestionTypePerformanceDto> performanceByType,
    
    // Recent Activity
    List<RecentQuizAttemptDto> recentAttempts,
    
    // Progress over Time
    List<ProgressDataDto> progressOverTime,
    
    // Strengths and Weaknesses
    StrengthsWeaknessesDto strengthsWeaknesses
) {
    /**
     * Overview statistics
     */
    public record OverviewStats(
        Long totalQuizzesTaken,
        Long totalQuizzesPassed,
        Long totalQuizzesFailed,
        BigDecimal overallPassRate,
        BigDecimal averageScore,
        BigDecimal highestScore,
        BigDecimal lowestScore,
        Integer totalTimeSpentMinutes,
        Long totalQuestionsAnswered,
        Long totalCorrectAnswers,
        BigDecimal overallAccuracy
    ) {}
    
    /**
     * Performance breakdown by question type
     */
    public record QuestionTypePerformanceDto(
        String questionType,
        Long totalAnswered,
        Long correctAnswers,
        BigDecimal accuracy,
        Integer averageTimeSeconds
    ) {}
    
    /**
     * Recent quiz attempt summary
     */
    public record RecentQuizAttemptDto(
        UUID sessionId,
        UUID quizId,
        String quizTitle,
        BigDecimal score,
        Boolean isPassed,
        Integer timeSpentSeconds,
        LocalDateTime completedAt
    ) {}
    
    /**
     * Progress data point for chart
     */
    public record ProgressDataDto(
        String period,        // "2026-01", "Week 1", etc.
        Long quizzesTaken,
        BigDecimal averageScore,
        BigDecimal passRate
    ) {}
    
    /**
     * Strengths and weaknesses analysis
     */
    public record StrengthsWeaknessesDto(
        List<String> strengths,    // Question types with high accuracy
        List<String> weaknesses,   // Question types with low accuracy
        List<String> recommendations
    ) {}
}
