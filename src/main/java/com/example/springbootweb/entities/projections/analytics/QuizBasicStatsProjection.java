package com.example.springbootweb.entities.projections.analytics;

/**
 * Projection interface for basic quiz statistics.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface QuizBasicStatsProjection {
    Long getTotalAttempts();
    Long getCompletedAttempts();
    Long getPassedAttempts();
    Long getFailedAttempts();
}
