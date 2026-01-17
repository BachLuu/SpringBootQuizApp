package com.example.springbootweb.entities.dtos.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for Admin Dashboard
 * Contains system-wide statistics and overview
 */
public record AdminDashboardResponse(
    // System Overview
    SystemOverviewDto systemOverview,
    
    // Activity Statistics
    ActivityStatsDto activityStats,
    
    // Top Performers
    List<TopPerformerDto> topPerformers,
    
    // Popular Quizzes
    List<PopularQuizDto> popularQuizzes,
    
    // Recent Activity
    List<RecentActivityDto> recentActivities,
    
    // Quiz Completion Rates
    List<QuizCompletionDto> quizCompletionRates
) {
    /**
     * System-wide counts and overview
     */
    public record SystemOverviewDto(
        Long totalUsers,
        Long activeUsers,
        Long totalQuizzes,
        Long activeQuizzes,
        Long totalQuestions,
        Long totalAttempts,
        Long completedAttempts,
        BigDecimal overallPassRate
    ) {}
    
    /**
     * Activity statistics for dashboard
     */
    public record ActivityStatsDto(
        Long attemptsToday,
        Long attemptsThisWeek,
        Long attemptsThisMonth,
        Long newUsersThisWeek,
        Long newUsersThisMonth,
        BigDecimal averageScoreThisWeek,
        BigDecimal averageScoreThisMonth
    ) {}
    
    /**
     * Top performer entry
     */
    public record TopPerformerDto(
        Integer rank,
        UUID userId,
        String userName,
        String userEmail,
        Long quizzesTaken,
        Long quizzesPassed,
        BigDecimal averageScore,
        BigDecimal passRate
    ) {}
    
    /**
     * Popular quiz entry
     */
    public record PopularQuizDto(
        UUID quizId,
        String quizTitle,
        Long totalAttempts,
        Long completedAttempts,
        BigDecimal passRate,
        BigDecimal averageScore
    ) {}
    
    /**
     * Recent activity entry
     */
    public record RecentActivityDto(
        UUID sessionId,
        UUID userId,
        String userName,
        UUID quizId,
        String quizTitle,
        String status,
        BigDecimal score,
        Boolean isPassed,
        LocalDateTime completedAt
    ) {}
    
    /**
     * Quiz completion rate entry
     */
    public record QuizCompletionDto(
        UUID quizId,
        String quizTitle,
        Long totalAttempts,
        Long completedAttempts,
        Long abandonedAttempts,
        BigDecimal completionRate,
        BigDecimal passRate
    ) {}
}
