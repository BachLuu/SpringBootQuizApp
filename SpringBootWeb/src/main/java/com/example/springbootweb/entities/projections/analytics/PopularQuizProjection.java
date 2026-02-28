package com.example.springbootweb.entities.projections.analytics;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Projection interface for popular quizzes.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface PopularQuizProjection {
    UUID getQuizId();
    Long getTotalAttempts();
    Long getCompletedAttempts();
    BigDecimal getAvgScore();
    Double getPassRate();
}
