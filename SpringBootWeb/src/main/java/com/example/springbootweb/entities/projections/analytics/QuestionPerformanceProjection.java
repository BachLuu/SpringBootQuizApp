package com.example.springbootweb.entities.projections.analytics;

import java.util.UUID;

import com.example.springbootweb.entities.enums.QuestionType;

/**
 * Projection interface for question performance statistics.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface QuestionPerformanceProjection {
    UUID getQuestionId();
    String getContent();
    QuestionType getType();
    Long getTotalAnswers();
    Long getCorrectAnswers();
    Long getIncorrectAnswers();
    Double getAvgTime();
}
