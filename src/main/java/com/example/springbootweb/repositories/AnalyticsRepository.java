package com.example.springbootweb.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbootweb.entities.models.QuizSession;
import com.example.springbootweb.entities.projections.analytics.ActivityStatsProjection;
import com.example.springbootweb.entities.projections.analytics.AnswerDistributionProjection;
import com.example.springbootweb.entities.projections.analytics.PopularQuizProjection;
import com.example.springbootweb.entities.projections.analytics.QuestionDifficultyProjection;
import com.example.springbootweb.entities.projections.analytics.QuestionPerformanceProjection;
import com.example.springbootweb.entities.projections.analytics.QuestionTimeAnalysisProjection;
import com.example.springbootweb.entities.projections.analytics.QuestionTypePerformanceProjection;
import com.example.springbootweb.entities.projections.analytics.QuizBasicStatsProjection;
import com.example.springbootweb.entities.projections.analytics.QuizCompletionProjection;
import com.example.springbootweb.entities.projections.analytics.ScoreDistributionProjection;
import com.example.springbootweb.entities.projections.analytics.ScoreStatsProjection;
import com.example.springbootweb.entities.projections.analytics.TimeStatsProjection;
import com.example.springbootweb.entities.projections.analytics.TopPerformerProjection;
import com.example.springbootweb.entities.projections.analytics.UserAnswerStatsProjection;
import com.example.springbootweb.entities.projections.analytics.UserOverviewStatsProjection;
import com.example.springbootweb.entities.projections.analytics.UserProgressProjection;

/**
 * Repository for Analytics queries. Contains complex aggregate queries for statistics and
 * reporting. Uses Interface-Based Projections for type-safe query results.
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<QuizSession, UUID> {

	// ==================== Quiz Statistics ====================

	/**
	 * Get basic quiz statistics
	 */
	@Query("""
			    SELECT
			        COUNT(qs) as totalAttempts,
			        COUNT(CASE WHEN qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') THEN 1 END) as completedAttempts,
			        COUNT(CASE WHEN qs.isPassed = true THEN 1 END) as passedAttempts,
			        COUNT(CASE WHEN qs.isPassed = false AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') THEN 1 END) as failedAttempts
			    FROM QuizSession qs
			    WHERE qs.quizId = :quizId
			""")
	QuizBasicStatsProjection getQuizBasicStatistics(@Param("quizId") UUID quizId);

	/**
	 * Get score statistics for a quiz
	 */
	@Query("""
			    SELECT
			        AVG(qs.score) as avgScore,
			        MAX(qs.score) as maxScore,
			        MIN(qs.score) as minScore
			    FROM QuizSession qs
			    WHERE qs.quizId = :quizId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			""")
	ScoreStatsProjection getQuizScoreStatistics(@Param("quizId") UUID quizId);

	/**
	 * Get time statistics for a quiz
	 */
	@Query("""
			    SELECT
			        AVG(qs.timeSpentSeconds) as avgTime,
			        MIN(qs.timeSpentSeconds) as minTime,
			        MAX(qs.timeSpentSeconds) as maxTime
			    FROM QuizSession qs
			    WHERE qs.quizId = :quizId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			""")
	TimeStatsProjection getQuizTimeStatistics(@Param("quizId") UUID quizId);

	/**
	 * Get score distribution for a quiz
	 */
	@Query("""
			    SELECT
			        CASE
			            WHEN qs.score BETWEEN 0 AND 10 THEN '0-10'
			            WHEN qs.score BETWEEN 11 AND 20 THEN '11-20'
			            WHEN qs.score BETWEEN 21 AND 30 THEN '21-30'
			            WHEN qs.score BETWEEN 31 AND 40 THEN '31-40'
			            WHEN qs.score BETWEEN 41 AND 50 THEN '41-50'
			            WHEN qs.score BETWEEN 51 AND 60 THEN '51-60'
			            WHEN qs.score BETWEEN 61 AND 70 THEN '61-70'
			            WHEN qs.score BETWEEN 71 AND 80 THEN '71-80'
			            WHEN qs.score BETWEEN 81 AND 90 THEN '81-90'
			            ELSE '91-100'
			        END as scoreRange,
			        COUNT(qs) as count
			    FROM QuizSession qs
			    WHERE qs.quizId = :quizId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    GROUP BY
			        CASE
			            WHEN qs.score BETWEEN 0 AND 10 THEN '0-10'
			            WHEN qs.score BETWEEN 11 AND 20 THEN '11-20'
			            WHEN qs.score BETWEEN 21 AND 30 THEN '21-30'
			            WHEN qs.score BETWEEN 31 AND 40 THEN '31-40'
			            WHEN qs.score BETWEEN 41 AND 50 THEN '41-50'
			            WHEN qs.score BETWEEN 51 AND 60 THEN '51-60'
			            WHEN qs.score BETWEEN 61 AND 70 THEN '61-70'
			            WHEN qs.score BETWEEN 71 AND 80 THEN '71-80'
			            WHEN qs.score BETWEEN 81 AND 90 THEN '81-90'
			            ELSE '91-100'
			        END
			    ORDER BY scoreRange
			""")
	List<ScoreDistributionProjection> getScoreDistribution(@Param("quizId") UUID quizId);

	// ==================== Question Performance ====================

	/**
	 * Get question performance statistics for a quiz
	 */
	@Query("""
			    SELECT
			        sa.questionId as questionId,
			        q.content as content,
			        q.questionType as type,
			        COUNT(sa) as totalAnswers,
			        COUNT(CASE WHEN sa.isCorrect = true THEN 1 END) as correctAnswers,
			        COUNT(CASE WHEN sa.isCorrect = false THEN 1 END) as incorrectAnswers,
			        AVG(sa.timeSpentSeconds) as avgTime
			    FROM SessionAnswer sa
			    JOIN sa.question q
			    JOIN sa.quizSession qs
			    WHERE qs.quizId = :quizId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    GROUP BY sa.questionId, q.content, q.questionType
			    ORDER BY
			        (CAST(COUNT(CASE WHEN sa.isCorrect = true THEN 1 END) AS double) /
			         NULLIF(CAST(COUNT(sa) AS double), 0)) ASC
			""")
	List<QuestionPerformanceProjection> getQuestionPerformance(@Param("quizId") UUID quizId);

	// ==================== User Performance ====================

	/**
	 * Get user overview statistics
	 */
	@Query("""
			    SELECT
			        COUNT(qs) as totalQuizzes,
			        COUNT(CASE WHEN qs.isPassed = true THEN 1 END) as passedQuizzes,
			        COUNT(CASE WHEN qs.isPassed = false AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') THEN 1 END) as failedQuizzes,
			        AVG(qs.score) as avgScore,
			        MAX(qs.score) as maxScore,
			        MIN(qs.score) as minScore,
			        SUM(qs.timeSpentSeconds) as totalTimeSeconds
			    FROM QuizSession qs
			    WHERE qs.userId = :userId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			""")
	UserOverviewStatsProjection getUserOverviewStats(@Param("userId") UUID userId);

	/**
	 * Get user's total questions answered and correct
	 */
	@Query("""
			    SELECT
			        COUNT(sa) as totalAnswered,
			        COUNT(CASE WHEN sa.isCorrect = true THEN 1 END) as totalCorrect
			    FROM SessionAnswer sa
			    JOIN sa.quizSession qs
			    WHERE qs.userId = :userId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			""")
	UserAnswerStatsProjection getUserAnswerStats(@Param("userId") UUID userId);

	/**
	 * Get user performance by question type
	 */
	@Query("""
			    SELECT
			        q.questionType as type,
			        COUNT(sa) as totalAnswered,
			        COUNT(CASE WHEN sa.isCorrect = true THEN 1 END) as correctAnswers,
			        AVG(sa.timeSpentSeconds) as avgTime
			    FROM SessionAnswer sa
			    JOIN sa.question q
			    JOIN sa.quizSession qs
			    WHERE qs.userId = :userId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    GROUP BY q.questionType
			""")
	List<QuestionTypePerformanceProjection> getUserPerformanceByQuestionType(@Param("userId") UUID userId);

	/**
	 * Get user's recent quiz attempts
	 */
	@Query("""
			    SELECT qs
			    FROM QuizSession qs
			    WHERE qs.userId = :userId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    ORDER BY qs.finishedAt DESC
			""")
	List<QuizSession> getUserRecentAttempts(@Param("userId") UUID userId, Pageable pageable);

	/**
	 * Get user progress over time (monthly)
	 */
	@Query("""
			    SELECT
			        FUNCTION('TO_CHAR', qs.finishedAt, 'YYYY-MM') as period,
			        COUNT(qs) as quizzesTaken,
			        AVG(qs.score) as avgScore,
			        (CAST(COUNT(CASE WHEN qs.isPassed = true THEN 1 END) AS double) /
			         NULLIF(CAST(COUNT(qs) AS double), 0) * 100) as passRate
			    FROM QuizSession qs
			    WHERE qs.userId = :userId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    AND qs.finishedAt >= :startDate
			    GROUP BY FUNCTION('TO_CHAR', qs.finishedAt, 'YYYY-MM')
			    ORDER BY period
			""")
	List<UserProgressProjection> getUserProgressOverTime(@Param("userId") UUID userId,
			@Param("startDate") LocalDateTime startDate);

	// ==================== Admin Dashboard ====================

	/**
	 * Count active users (users who have taken at least one quiz)
	 */
	@Query("SELECT COUNT(DISTINCT qs.userId) FROM QuizSession qs")
	Long countActiveUsers();

	/**
	 * Get activity statistics
	 */
	@Query("""
			    SELECT
			        COUNT(CASE WHEN qs.createdAt >= :todayStart THEN 1 END) as attemptsToday,
			        COUNT(CASE WHEN qs.createdAt >= :weekStart THEN 1 END) as attemptsThisWeek,
			        COUNT(CASE WHEN qs.createdAt >= :monthStart THEN 1 END) as attemptsThisMonth
			    FROM QuizSession qs
			    WHERE qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			""")
	ActivityStatsProjection getActivityStats(@Param("todayStart") LocalDateTime todayStart,
			@Param("weekStart") LocalDateTime weekStart, @Param("monthStart") LocalDateTime monthStart);

	/**
	 * Get average score for time period
	 */
	@Query("""
			    SELECT AVG(qs.score)
			    FROM QuizSession qs
			    WHERE qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    AND qs.finishedAt >= :startDate
			""")
	BigDecimal getAverageScoreSince(@Param("startDate") LocalDateTime startDate);

	/**
	 * Get top performers
	 */
	@Query("""
			    SELECT
			        qs.userId as userId,
			        COUNT(qs) as quizzesTaken,
			        COUNT(CASE WHEN qs.isPassed = true THEN 1 END) as quizzesPassed,
			        AVG(qs.score) as avgScore
			    FROM QuizSession qs
			    WHERE qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    GROUP BY qs.userId
			    ORDER BY avgScore DESC, quizzesPassed DESC
			""")
	List<TopPerformerProjection> getTopPerformers(Pageable pageable);

	/**
	 * Get popular quizzes
	 */
	@Query("""
			    SELECT
			        qs.quizId as quizId,
			        COUNT(qs) as totalAttempts,
			        COUNT(CASE WHEN qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') THEN 1 END) as completedAttempts,
			        AVG(CASE WHEN qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') THEN qs.score END) as avgScore,
			        (CAST(COUNT(CASE WHEN qs.isPassed = true THEN 1 END) AS double) /
			         NULLIF(CAST(COUNT(CASE WHEN qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') THEN 1 END) AS double), 0) * 100) as passRate
			    FROM QuizSession qs
			    GROUP BY qs.quizId
			    ORDER BY totalAttempts DESC
			""")
	List<PopularQuizProjection> getPopularQuizzes(Pageable pageable);

	/**
	 * Get recent completed activities
	 */
	@Query("""
			    SELECT qs
			    FROM QuizSession qs
			    WHERE qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    ORDER BY qs.finishedAt DESC
			""")
	List<QuizSession> getRecentCompletedActivities(Pageable pageable);

	/**
	 * Get quiz completion rates
	 */
	@Query("""
			    SELECT
			        qs.quizId as quizId,
			        COUNT(qs) as totalAttempts,
			        COUNT(CASE WHEN qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') THEN 1 END) as completedAttempts,
			        COUNT(CASE WHEN qs.status = 'ABANDONED' THEN 1 END) as abandonedAttempts
			    FROM QuizSession qs
			    GROUP BY qs.quizId
			""")
	List<QuizCompletionProjection> getQuizCompletionRates();

	/**
	 * Count overall pass rate
	 */
	@Query("""
			    SELECT
			        (CAST(COUNT(CASE WHEN qs.isPassed = true THEN 1 END) AS double) /
			         NULLIF(CAST(COUNT(CASE WHEN qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT') THEN 1 END) AS double), 0) * 100)
			    FROM QuizSession qs
			""")
	BigDecimal getOverallPassRate();

	// ==================== Question Difficulty Analysis ====================

	/**
	 * Get question difficulty metrics
	 */
	@Query("""
			    SELECT
			        COUNT(sa) as totalAttempts,
			        COUNT(CASE WHEN sa.isCorrect = true THEN 1 END) as correctAttempts,
			        COUNT(CASE WHEN sa.isCorrect = false THEN 1 END) as incorrectAttempts,
			        COUNT(CASE WHEN sa.answerId IS NULL AND sa.textResponse IS NULL THEN 1 END) as skippedAttempts
			    FROM SessionAnswer sa
			    JOIN sa.quizSession qs
			    WHERE sa.questionId = :questionId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			""")
	QuestionDifficultyProjection getQuestionDifficultyMetrics(@Param("questionId") UUID questionId);

	/**
	 * Get answer distribution for a question
	 */
	@Query("""
			    SELECT
			        sa.answerId as answerId,
			        a.content as content,
			        a.isCorrect as isCorrect,
			        COUNT(sa) as selectedCount
			    FROM SessionAnswer sa
			    JOIN sa.answer a
			    JOIN sa.quizSession qs
			    WHERE sa.questionId = :questionId
			    AND sa.answerId IS NOT NULL
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			    GROUP BY sa.answerId, a.content, a.isCorrect
			    ORDER BY selectedCount DESC
			""")
	List<AnswerDistributionProjection> getAnswerDistribution(@Param("questionId") UUID questionId);

	/**
	 * Get time analysis for a question
	 */
	@Query("""
			    SELECT
			        AVG(sa.timeSpentSeconds) as avgTime,
			        MIN(sa.timeSpentSeconds) as minTime,
			        MAX(sa.timeSpentSeconds) as maxTime,
			        AVG(CASE WHEN sa.isCorrect = true THEN sa.timeSpentSeconds END) as avgTimeCorrect,
			        AVG(CASE WHEN sa.isCorrect = false THEN sa.timeSpentSeconds END) as avgTimeIncorrect
			    FROM SessionAnswer sa
			    JOIN sa.quizSession qs
			    WHERE sa.questionId = :questionId
			    AND qs.status IN ('SUBMITTED', 'GRADED', 'TIMED_OUT')
			""")
	QuestionTimeAnalysisProjection getQuestionTimeAnalysis(@Param("questionId") UUID questionId);

	// ==================== New Users Count ====================

	/**
	 * Count new users since date
	 */
	@Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
	Long countNewUsersSince(@Param("startDate") LocalDateTime startDate);

}
