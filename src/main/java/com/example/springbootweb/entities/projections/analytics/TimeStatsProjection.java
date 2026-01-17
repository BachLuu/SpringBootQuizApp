package com.example.springbootweb.entities.projections.analytics;

/**
 * Projection interface for time statistics.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface TimeStatsProjection {
    Double getAvgTime();
    Integer getMinTime();
    Integer getMaxTime();
}
