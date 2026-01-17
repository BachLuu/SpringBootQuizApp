package com.example.springbootweb.entities.projections.analytics;

import java.math.BigDecimal;

/**
 * Projection interface for user overview statistics.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface UserOverviewStatsProjection {
    Long getTotalQuizzes();
    Long getPassedQuizzes();
    Long getFailedQuizzes();
    BigDecimal getAvgScore();
    BigDecimal getMaxScore();
    BigDecimal getMinScore();
    Long getTotalTimeSeconds();
}
