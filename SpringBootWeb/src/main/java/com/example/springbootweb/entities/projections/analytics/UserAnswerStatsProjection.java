package com.example.springbootweb.entities.projections.analytics;

/**
 * Projection interface for user answer statistics.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface UserAnswerStatsProjection {
    Long getTotalAnswered();
    Long getTotalCorrect();
}
