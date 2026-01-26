package com.example.springbootweb.entities.dtos.analytics;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.example.springbootweb.entities.enums.DifficultyLevel;

/**
 * Response DTO for Question Difficulty Analysis
 * Analyzes how difficult a question is based on user performance
 */
public record QuestionDifficultyResponse(
    UUID questionId,
    String questionContent,
    String questionType,
    
    // Difficulty Metrics
    DifficultyMetricsDto metrics,
    
    // Answer Distribution
    List<AnswerDistributionDto> answerDistribution,
    
    // Time Analysis
    TimeAnalysisDto timeAnalysis,
    
    // Recommendations
    List<String> recommendations
) {
    /**
     * Difficulty metrics for the question
     */
    public record DifficultyMetricsDto(
        Long totalAttempts,
        Long correctAttempts,
        Long incorrectAttempts,
        Long skippedAttempts,
        BigDecimal correctRate,
        BigDecimal incorrectRate,
        BigDecimal skippedRate,
        DifficultyLevel difficultyLevel,
        BigDecimal difficultyScore  // 0-100 scale
    ) {}
    
    /**
     * Distribution of answers for MCQ questions
     */
    public record AnswerDistributionDto(
        UUID answerId,
        String answerContent,
        Boolean isCorrect,
        Long selectedCount,
        BigDecimal selectionRate
    ) {}
    
    /**
     * Time analysis for the question
     */
    public record TimeAnalysisDto(
        Integer averageTimeSeconds,
        Integer medianTimeSeconds,
        Integer fastestTimeSeconds,
        Integer slowestTimeSeconds,
        Integer avgTimeCorrectAnswers,
        Integer avgTimeIncorrectAnswers
    ) {}
}
