package com.example.springbootweb.entities.dtos.quizsessions;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO to submit an answer for a question
 */
public record SubmitAnswerRequest(
    @NotNull(message = "Question ID is required")
    UUID questionId,

    /**
     * Selected answer ID (for MCQ, Single Choice, True/False)
     * Can be null for text-based questions
     */
    UUID answerId,

    /**
     * Selected answer IDs (for Multiple Choice questions)
     */
    List<UUID> answerIds,

    /**
     * Text response (for fill-in-blank, short/long answer)
     */
    String textResponse,

    /**
     * Time spent on this question in seconds
     */
    Integer timeSpentSeconds
) {}
