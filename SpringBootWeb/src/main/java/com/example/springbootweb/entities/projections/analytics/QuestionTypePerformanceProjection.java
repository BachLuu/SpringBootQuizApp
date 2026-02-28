package com.example.springbootweb.entities.projections.analytics;

import com.example.springbootweb.entities.enums.QuestionType;

/**
 * Projection interface for user performance by question type.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface QuestionTypePerformanceProjection {
    QuestionType getType();
    Long getTotalAnswered();
    Long getCorrectAnswers();
    Double getAvgTime();
}
