package com.example.springbootweb.entities.projections.analytics;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Projection interface for top performers.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface TopPerformerProjection {
    UUID getUserId();
    Long getQuizzesTaken();
    Long getQuizzesPassed();
    BigDecimal getAvgScore();
}
