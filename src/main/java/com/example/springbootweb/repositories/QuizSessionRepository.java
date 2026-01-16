package com.example.springbootweb.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbootweb.entities.enums.QuizSessionStatus;
import com.example.springbootweb.entities.models.QuizSession;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, UUID> {

	/**
	 * Find all sessions by user ID
	 */
	List<QuizSession> findByUserIdOrderByCreatedAtDesc(UUID userId);

	/**
	 * Find all sessions by user ID with pagination
	 */
	Page<QuizSession> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

	/**
	 * Find all sessions for a specific quiz
	 */
	List<QuizSession> findByQuizIdOrderByCreatedAtDesc(UUID quizId);

	/**
	 * Find session by user and quiz (to check if already attempted)
	 */
	Optional<QuizSession> findByUserIdAndQuizIdAndStatus(UUID userId, UUID quizId, QuizSessionStatus status);

	/**
	 * Find all in-progress sessions for a user
	 */
	List<QuizSession> findByUserIdAndStatus(UUID userId, QuizSessionStatus status);

	/**
	 * Find all sessions by quiz and status
	 */
	List<QuizSession> findByQuizIdAndStatus(UUID quizId, QuizSessionStatus status);

	/**
	 * Check if user has an active session for this quiz
	 */
	@Query("SELECT CASE WHEN COUNT(qs) > 0 THEN true ELSE false END " + "FROM QuizSession qs "
			+ "WHERE qs.userId = :userId " + "AND qs.quizId = :quizId "
			+ "AND qs.status IN ('NOT_STARTED', 'IN_PROGRESS', 'PAUSED')")
	boolean hasActiveSession(@Param("userId") UUID userId, @Param("quizId") UUID quizId);

	/**
	 * Get leaderboard for a quiz (completed sessions ordered by score)
	 */
	@Query("SELECT qs FROM QuizSession qs " + "WHERE qs.quizId = :quizId "
			+ "AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') "
			+ "ORDER BY qs.score DESC, qs.timeSpentSeconds ASC")
	List<QuizSession> findLeaderboard(@Param("quizId") UUID quizId);

	/**
	 * Get leaderboard with pagination
	 */
	@Query("SELECT qs FROM QuizSession qs " + "WHERE qs.quizId = :quizId "
			+ "AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') "
			+ "ORDER BY qs.score DESC, qs.timeSpentSeconds ASC")
	Page<QuizSession> findLeaderboard(@Param("quizId") UUID quizId, Pageable pageable);

	/**
	 * Count completed sessions for a quiz
	 */
	@Query("SELECT COUNT(qs) FROM QuizSession qs " + "WHERE qs.quizId = :quizId "
			+ "AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')")
	long countCompletedSessions(@Param("quizId") UUID quizId);

	/**
	 * Get user's rank for a quiz
	 */
	@Query("SELECT COUNT(qs) + 1 FROM QuizSession qs " + "WHERE qs.quizId = :quizId "
			+ "AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') "
			+ "AND (qs.score > :score OR (qs.score = :score AND qs.timeSpentSeconds < :timeSpent))")
	int getUserRank(@Param("quizId") UUID quizId, @Param("score") java.math.BigDecimal score,
			@Param("timeSpent") Integer timeSpent);

	/**
	 * Find expired sessions that need to be auto-submitted
	 */
	@Query("SELECT qs FROM QuizSession qs " + "WHERE qs.status = 'IN_PROGRESS' " + "AND qs.expiresAt < :now")
	List<QuizSession> findExpiredSessions(@Param("now") LocalDateTime now);

	/**
	 * Update session status
	 */
	@Modifying
	@Query("UPDATE QuizSession qs SET qs.status = :status WHERE qs.id = :id")
	void updateStatus(@Param("id") UUID id, @Param("status") QuizSessionStatus status);

	/**
	 * Count total attempts by user
	 */
	long countByUserId(UUID userId);

	/**
	 * Count passed sessions by user
	 */
	long countByUserIdAndIsPassedTrue(UUID userId);

	/**
	 * Get average score for a quiz
	 */
	@Query("SELECT AVG(qs.score) FROM QuizSession qs " + "WHERE qs.quizId = :quizId "
			+ "AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')")
	Double getAverageScore(@Param("quizId") UUID quizId);

	/**
	 * Count users who passed a quiz
	 */
	@Query("SELECT COUNT(qs) FROM QuizSession qs " + "WHERE qs.quizId = :quizId " + "AND qs.isPassed = true")
	long countPassedByQuiz(@Param("quizId") UUID quizId);

}
