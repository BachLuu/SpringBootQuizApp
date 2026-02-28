package com.example.springbootweb.entities.projections.analytics;

import java.util.UUID;

/**
 * Projection interface for answer distribution.
 * Used by Spring Data JPA to map query results in a type-safe manner.
 */
public interface AnswerDistributionProjection {
    UUID getAnswerId();
    String getContent();
    Boolean getIsCorrect();
    Long getSelectedCount();
}
