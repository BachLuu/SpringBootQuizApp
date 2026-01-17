package com.example.springbootweb.entities.projections.analytics;

/**
 * Projection interface for question time analysis.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface QuestionTimeAnalysisProjection {
    Double getAvgTime();
    Integer getMinTime();
    Integer getMaxTime();
    Double getAvgTimeCorrect();
    Double getAvgTimeIncorrect();
}
