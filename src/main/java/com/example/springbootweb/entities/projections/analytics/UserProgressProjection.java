package com.example.springbootweb.entities.projections.analytics;

import java.math.BigDecimal;

/**
 * Projection interface for user progress over time.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface UserProgressProjection {
    String getPeriod();
    Long getQuizzesTaken();
    BigDecimal getAvgScore();
    Double getPassRate();
}
