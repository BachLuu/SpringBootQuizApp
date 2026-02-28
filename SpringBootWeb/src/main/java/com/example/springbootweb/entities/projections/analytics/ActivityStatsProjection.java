package com.example.springbootweb.entities.projections.analytics;

/**
 * Projection interface for activity statistics.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface ActivityStatsProjection {
    Long getAttemptsToday();
    Long getAttemptsThisWeek();
    Long getAttemptsThisMonth();
}
