package com.example.springbootweb.services.interfaces;

import java.util.UUID;

import com.example.springbootweb.entities.dtos.analytics.AdminDashboardResponse;
import com.example.springbootweb.entities.dtos.analytics.QuestionDifficultyResponse;
import com.example.springbootweb.entities.dtos.analytics.QuizStatisticsResponse;
import com.example.springbootweb.entities.dtos.analytics.UserPerformanceResponse;

/**
 * Service interface for Analytics operations. Provides complex statistics and reporting
 * capabilities.
 */
public interface IAnalyticsService {

	/**
	 * Get comprehensive statistics for a quiz. Includes pass/fail rates, question
	 * performance, and score distribution.
	 * @param quizId The quiz ID
	 * @return QuizStatisticsResponse with detailed statistics
	 */
	QuizStatisticsResponse getQuizStatistics(UUID quizId);

	/**
	 * Get user performance dashboard. Includes quiz history, strengths/weaknesses, and
	 * progress over time.
	 * @param userId The user ID
	 * @return UserPerformanceResponse with performance data
	 */
	UserPerformanceResponse getUserPerformance(UUID userId);

	/**
	 * Get admin dashboard overview. Includes system-wide statistics, top performers, and
	 * activity metrics.
	 * @return AdminDashboardResponse with dashboard data
	 */
	AdminDashboardResponse getAdminDashboard();

	/**
	 * Get difficulty analysis for a question. Analyzes correct/incorrect rates, answer
	 * distribution, and timing.
	 * @param questionId The question ID
	 * @return QuestionDifficultyResponse with difficulty analysis
	 */
	QuestionDifficultyResponse getQuestionDifficultyAnalysis(UUID questionId);

}
