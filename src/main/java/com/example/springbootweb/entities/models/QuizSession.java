package com.example.springbootweb.entities.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.springbootweb.entities.enums.QuizSessionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a quiz attempt session by a user.
 * Tracks the progress, timing, and results of a quiz attempt.
 */
@Entity
@Table(name = "quiz_sessions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizSession {

    @Id
    @UuidGenerator
    private UUID id;

    /**
     * The user taking the quiz
     */
    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * The quiz being taken
     */
    @NotNull
    @Column(name = "quiz_id", nullable = false)
    private UUID quizId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    /**
     * Current status of the session
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private QuizSessionStatus status = QuizSessionStatus.NOT_STARTED;

    /**
     * When the session was created
     */
    @NotNull
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * When the user actually started the quiz
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * When the quiz was submitted/finished
     */
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * When the quiz should expire (startedAt + quiz.duration)
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Total time spent in seconds (excluding paused time)
     */
    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    /**
     * Number of correct answers
     */
    @Column(name = "correct_answers")
    @Builder.Default
    private Integer correctAnswers = 0;

    /**
     * Total number of questions in this session
     */
    @Column(name = "total_questions")
    @Builder.Default
    private Integer totalQuestions = 0;

    /**
     * Number of questions answered
     */
    @Column(name = "answered_questions")
    @Builder.Default
    private Integer answeredQuestions = 0;

    /**
     * Score as percentage (0-100)
     */
    @Column(name = "score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal score = BigDecimal.ZERO;

    /**
     * Raw score (points earned)
     */
    @Column(name = "points_earned", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal pointsEarned = BigDecimal.ZERO;

    /**
     * Maximum possible points
     */
    @Column(name = "max_points", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal maxPoints = BigDecimal.ZERO;

    /**
     * Whether the user passed (score >= passing threshold)
     */
    @Column(name = "is_passed")
    private Boolean isPassed;

    /**
     * Current question index (for tracking progress)
     */
    @Column(name = "current_question_index")
    @Builder.Default
    private Integer currentQuestionIndex = 0;

    /**
     * User's answers for this session
     */
    @OneToMany(mappedBy = "quizSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<SessionAnswer> sessionAnswers = new ArrayList<>();
}
