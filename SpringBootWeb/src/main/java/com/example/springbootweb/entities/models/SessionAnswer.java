package com.example.springbootweb.entities.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user's answer to a question within a quiz session.
 * This is different from UserAnswer - it's specifically for quiz session tracking.
 */
@Entity
@Table(name = "session_answers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionAnswer {

    @Id
    @UuidGenerator
    private UUID id;

    /**
     * The quiz session this answer belongs to
     */
    @NotNull
    @Column(name = "quiz_session_id", nullable = false)
    private UUID quizSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_session_id", insertable = false, updatable = false)
    private QuizSession quizSession;

    /**
     * The question being answered
     */
    @NotNull
    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    /**
     * The selected answer (for MCQ, Single Choice, True/False)
     * Can be null for text-based answers
     */
    @Column(name = "answer_id")
    private UUID answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", insertable = false, updatable = false)
    private Answer answer;

    /**
     * Text response for fill-in-the-blank, short answer, long answer questions
     */
    @Column(name = "text_response", columnDefinition = "TEXT")
    private String textResponse;

    /**
     * Whether the answer is correct (auto-graded for MCQ types)
     */
    @Column(name = "is_correct")
    private Boolean isCorrect;

    /**
     * Points awarded for this answer
     */
    @Column(name = "points_awarded", precision = 10, scale = 2)
    @Builder.Default
    private java.math.BigDecimal pointsAwarded = java.math.BigDecimal.ZERO;

    /**
     * When the answer was submitted
     */
    @Column(name = "answered_at")
    @Builder.Default
    private LocalDateTime answeredAt = LocalDateTime.now();

    /**
     * Time spent on this question in seconds
     */
    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    /**
     * Order in which this question was answered
     */
    @Column(name = "answer_order")
    private Integer answerOrder;

    /**
     * Whether this answer has been manually reviewed (for essay questions)
     */
    @Column(name = "is_reviewed")
    @Builder.Default
    private Boolean isReviewed = false;

    /**
     * Reviewer's feedback (for manual review)
     */
    @Column(name = "reviewer_feedback", columnDefinition = "TEXT")
    private String reviewerFeedback;
}
