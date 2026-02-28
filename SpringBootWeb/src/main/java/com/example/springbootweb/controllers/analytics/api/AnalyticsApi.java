package com.example.springbootweb.controllers.analytics.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springbootweb.entities.dtos.analytics.AdminDashboardResponse;
import com.example.springbootweb.entities.dtos.analytics.QuestionDifficultyResponse;
import com.example.springbootweb.entities.dtos.analytics.QuizStatisticsResponse;
import com.example.springbootweb.entities.dtos.analytics.UserPerformanceResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API interface for Analytics endpoints.
 * Contains Swagger/OpenAPI documentation.
 */
@Tag(name = "Analytics", description = "Advanced analytics and reporting endpoints for quiz statistics, user performance, and admin dashboard")
@RequestMapping("/api/analytics")
@SecurityRequirement(name = "bearerAuth")
public interface AnalyticsApi {

    // ==================== Quiz Statistics ====================

    @Operation(
        summary = "Get Quiz Statistics",
        description = """
            Retrieves comprehensive statistics for a specific quiz including:
            - Pass/fail rates and completion metrics
            - Score distribution and average scores
            - Question-by-question performance analysis
            - Time spent statistics
            - Difficulty analysis per question
            
            This endpoint is useful for quiz creators and administrators to understand quiz performance.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved quiz statistics",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = QuizStatisticsResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Quiz not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content
        )
    })
    @GetMapping("/quiz/{quizId}/statistics")
    ResponseEntity<QuizStatisticsResponse> getQuizStatistics(
        @Parameter(
            description = "The unique identifier of the quiz",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @PathVariable UUID quizId
    );

    // ==================== User Performance ====================

    @Operation(
        summary = "Get User Performance Dashboard",
        description = """
            Retrieves comprehensive performance data for a specific user including:
            - Overview statistics (total quizzes, pass rate, average score)
            - Performance breakdown by question type
            - Recent quiz attempts with scores
            - Progress over time (monthly trends)
            - Strengths and weaknesses analysis with recommendations
            
            Users can view their own performance. Admins can view any user's performance.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user performance data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserPerformanceResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Cannot view other user's performance",
            content = @Content
        )
    })
    @GetMapping("/user/{userId}/performance")
    ResponseEntity<UserPerformanceResponse> getUserPerformance(
        @Parameter(
            description = "The unique identifier of the user",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @PathVariable UUID userId
    );

    // ==================== Admin Dashboard ====================

    @Operation(
        summary = "Get Admin Dashboard",
        description = """
            Retrieves the administrative dashboard with system-wide statistics including:
            - System overview (total users, quizzes, questions, attempts)
            - Activity statistics (attempts today, this week, this month)
            - Top performers ranked by average score
            - Most popular quizzes by attempt count
            - Recent completed activities
            - Quiz completion rates and abandonment analysis
            
            **Requires Admin role.**
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved admin dashboard data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AdminDashboardResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin role required",
            content = @Content
        )
    })
    @GetMapping("/admin/dashboard")
    ResponseEntity<AdminDashboardResponse> getAdminDashboard();

    // ==================== Question Difficulty Analysis ====================

    @Operation(
        summary = "Get Question Difficulty Analysis",
        description = """
            Retrieves detailed difficulty analysis for a specific question including:
            - Difficulty metrics (correct/incorrect/skipped rates)
            - Calculated difficulty level (VERY_EASY to VERY_HARD)
            - Answer distribution showing how users selected each option
            - Time analysis (average, fastest, slowest response times)
            - Recommendations for improving the question
            
            This endpoint helps quiz creators understand which questions need revision.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved question difficulty analysis",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = QuestionDifficultyResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Question not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content
        )
    })
    @GetMapping("/questions/{questionId}/difficulty-analysis")
    ResponseEntity<QuestionDifficultyResponse> getQuestionDifficultyAnalysis(
        @Parameter(
            description = "The unique identifier of the question",
            required = true,
            example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @PathVariable UUID questionId
    );
}
