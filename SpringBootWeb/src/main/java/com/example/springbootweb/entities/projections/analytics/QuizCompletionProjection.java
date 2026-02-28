package com.example.springbootweb.entities.projections.analytics;

import java.util.UUID;

/**
 * Projection interface for quiz completion rates.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface QuizCompletionProjection {
    UUID getQuizId();
    Long getTotalAttempts();
    Long getCompletedAttempts();
    Long getAbandonedAttempts();
}
