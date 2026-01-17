package com.example.springbootweb.entities.projections.analytics;

/**
 * Projection interface for question difficulty metrics.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface QuestionDifficultyProjection {
    Long getTotalAttempts();
    Long getCorrectAttempts();
    Long getIncorrectAttempts();
    Long getSkippedAttempts();
}
