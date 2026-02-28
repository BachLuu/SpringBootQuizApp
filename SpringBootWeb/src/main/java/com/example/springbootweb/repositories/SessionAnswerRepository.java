package com.example.springbootweb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbootweb.entities.models.SessionAnswer;

@Repository
public interface SessionAnswerRepository extends JpaRepository<SessionAnswer, UUID> {

    /**
     * Find all answers for a quiz session
     */
    List<SessionAnswer> findByQuizSessionIdOrderByAnswerOrder(UUID quizSessionId);

    /**
     * Find answer for a specific question in a session
     */
    Optional<SessionAnswer> findByQuizSessionIdAndQuestionId(UUID quizSessionId, UUID questionId);

    /**
     * Check if a question has been answered in a session
     */
    boolean existsByQuizSessionIdAndQuestionId(UUID quizSessionId, UUID questionId);

    /**
     * Count answered questions in a session
     */
    long countByQuizSessionId(UUID quizSessionId);

    /**
     * Count correct answers in a session
     */
    long countByQuizSessionIdAndIsCorrectTrue(UUID quizSessionId);

    /**
     * Get all correct answers for a session
     */
    List<SessionAnswer> findByQuizSessionIdAndIsCorrectTrue(UUID quizSessionId);

    /**
     * Delete all answers for a session
     */
    void deleteByQuizSessionId(UUID quizSessionId);

    /**
     * Get answers that need manual review
     */
    @Query("SELECT sa FROM SessionAnswer sa " +
           "WHERE sa.quizSessionId = :sessionId " +
           "AND sa.isReviewed = false " +
           "AND sa.textResponse IS NOT NULL")
    List<SessionAnswer> findAnswersNeedingReview(@Param("sessionId") UUID sessionId);

    /**
     * Count how many times each answer was selected for a question
     */
    @Query("SELECT sa.answerId, COUNT(sa) FROM SessionAnswer sa " +
           "WHERE sa.questionId = :questionId " +
           "AND sa.answerId IS NOT NULL " +
           "GROUP BY sa.answerId")
    List<Object[]> countAnswerSelections(@Param("questionId") UUID questionId);

    /**
     * Get average time spent on a question
     */
    @Query("SELECT AVG(sa.timeSpentSeconds) FROM SessionAnswer sa " +
           "WHERE sa.questionId = :questionId")
    Double getAverageTimeForQuestion(@Param("questionId") UUID questionId);

    /**
     * Get correct rate for a question
     */
    @Query("SELECT " +
           "CAST(SUM(CASE WHEN sa.isCorrect = true THEN 1 ELSE 0 END) AS double) / " +
           "CAST(COUNT(sa) AS double) * 100 " +
           "FROM SessionAnswer sa " +
           "WHERE sa.questionId = :questionId")
    Double getCorrectRateForQuestion(@Param("questionId") UUID questionId);
}
