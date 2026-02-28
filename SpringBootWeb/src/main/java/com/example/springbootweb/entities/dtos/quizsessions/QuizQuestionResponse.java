package com.example.springbootweb.entities.dtos.quizsessions;

import java.util.List;
import java.util.UUID;

import com.example.springbootweb.entities.enums.QuestionType;

/**
 * Response DTO for a question in the quiz session (without revealing correct answer)
 */
public record QuizQuestionResponse(
    UUID id,
    String content,
    QuestionType questionType,
    Integer questionNumber,
    Integer totalQuestions,
    List<QuizAnswerOption> options,
    Boolean isAnswered,
    UUID selectedAnswerId,
    String textResponse
) {
    /**
     * Answer option without revealing if it's correct
     */
    public record QuizAnswerOption(
        UUID id,
        String content
    ) {}
}
