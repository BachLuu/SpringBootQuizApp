package com.example.springbootweb.entities.dtos.quizsessions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for quiz leaderboard
 */
public record LeaderboardResponse(
    UUID quizId,
    String quizTitle,
    Integer totalParticipants,
    List<LeaderboardEntry> entries
) {
    /**
     * Individual leaderboard entry
     */
    public record LeaderboardEntry(
        Integer rank,
        UUID userId,
        String userName,
        String userAvatar,
        BigDecimal score,
        BigDecimal pointsEarned,
        Integer correctAnswers,
        Integer totalQuestions,
        Integer timeSpentSeconds,
        java.time.LocalDateTime completedAt
    ) {}
}
