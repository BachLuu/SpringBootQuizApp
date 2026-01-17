package com.example.springbootweb.controllers.analytics;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootweb.controllers.analytics.api.AnalyticsApi;
import com.example.springbootweb.entities.dtos.analytics.AdminDashboardResponse;
import com.example.springbootweb.entities.dtos.analytics.QuestionDifficultyResponse;
import com.example.springbootweb.entities.dtos.analytics.QuizStatisticsResponse;
import com.example.springbootweb.entities.dtos.analytics.UserPerformanceResponse;
import com.example.springbootweb.services.interfaces.IAnalyticsService;

import lombok.RequiredArgsConstructor;

/**
 * Controller implementation for Analytics endpoints.
 * Provides advanced analytics and reporting capabilities.
 */
@RestController
@RequiredArgsConstructor
public class AnalyticsController implements AnalyticsApi {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsController.class);

    private final IAnalyticsService analyticsService;

    // ==================== Quiz Statistics ====================

    @Override
    @PreAuthorize("hasAnyRole('Admin', 'User')")
    public ResponseEntity<QuizStatisticsResponse> getQuizStatistics(UUID quizId) {
        LOG.info("Request to get statistics for quiz: {}", quizId);
        QuizStatisticsResponse response = analyticsService.getQuizStatistics(quizId);
        return ResponseEntity.ok(response);
    }

    // ==================== User Performance ====================

    @Override
    @PreAuthorize("hasAnyRole('Admin', 'User')")
    public ResponseEntity<UserPerformanceResponse> getUserPerformance(UUID userId) {
        LOG.info("Request to get performance for user: {}", userId);
        // Note: Additional check can be added here to verify user can only access their own data
        // unless they are admin
        UserPerformanceResponse response = analyticsService.getUserPerformance(userId);
        return ResponseEntity.ok(response);
    }

    // ==================== Admin Dashboard ====================

    @Override
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        LOG.info("Request to get admin dashboard");
        AdminDashboardResponse response = analyticsService.getAdminDashboard();
        return ResponseEntity.ok(response);
    }

    // ==================== Question Difficulty Analysis ====================

    @Override
    @PreAuthorize("hasAnyRole('Admin', 'User')")
    public ResponseEntity<QuestionDifficultyResponse> getQuestionDifficultyAnalysis(UUID questionId) {
        LOG.info("Request to get difficulty analysis for question: {}", questionId);
        QuestionDifficultyResponse response = analyticsService.getQuestionDifficultyAnalysis(questionId);
        return ResponseEntity.ok(response);
    }
}
