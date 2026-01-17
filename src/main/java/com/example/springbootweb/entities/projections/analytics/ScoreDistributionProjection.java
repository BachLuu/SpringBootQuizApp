package com.example.springbootweb.entities.projections.analytics;

/**
 * Projection interface for score distribution.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface ScoreDistributionProjection {
    String getScoreRange();
    Long getCount();
}
