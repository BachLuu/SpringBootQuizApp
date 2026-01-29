package com.example.springbootweb.controllers.quizhistory.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.springbootweb.entities.dtos.quizsessions.LeaderboardResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionFilter;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionSummaryResponse;
import com.example.springbootweb.entities.enums.QuizSessionStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Interface for Quiz History and Leaderboard operations. Read-only endpoints for
 * historical data and rankings.
 */
@Tag(name = "Quiz History & Leaderboards", description = "Historical data and rankings APIs")
public interface QuizHistoryApi {

	// ==================== User History ====================

	@Operation(summary = "Get my history", description = "Get the authenticated user's quiz attempt history with optional filters")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
			@ApiResponse(responseCode = "204", description = "No quiz attempts found") })
	ResponseEntity<List<QuizSessionSummaryResponse>> getMyHistory(
			@ModelAttribute QuizSessionFilter quizSessionFilter,
			UserDetails userDetails);

	@Operation(summary = "Get my history (paginated)",
			description = "Get the authenticated user's quiz attempt history with pagination and optional filters")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "History retrieved successfully") })
	ResponseEntity<Page<QuizSessionSummaryResponse>> getMyHistoryPaged(
			@Parameter(description = "Page number (0-based)") int page,
			@Parameter(description = "Page size") int size,
			@ModelAttribute QuizSessionFilter quizSessionFilter,

			UserDetails userDetails);

	@Operation(summary = "Get user's history (Admin)",
			description = "Get a specific user's quiz attempt history with optional filters (admin only)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
			@ApiResponse(responseCode = "204", description = "No quiz attempts found"),
			@ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
			@ApiResponse(responseCode = "404", description = "User not found") })
	ResponseEntity<List<QuizSessionSummaryResponse>> getUserHistory(
			@Parameter(description = "User ID", required = true) UUID userId,
			@ModelAttribute QuizSessionFilter quizSessionFilter);

	// ==================== Leaderboard ====================

	@Operation(summary = "Get leaderboard", description = "Get the leaderboard for a specific quiz")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Quiz not found") })
	ResponseEntity<LeaderboardResponse> getLeaderboard(@Parameter(description = "Quiz ID", required = true) UUID quizId,
			@Parameter(description = "Number of top entries to return") int limit);

	@Operation(summary = "Get leaderboard (paginated)",
			description = "Get the leaderboard for a specific quiz with pagination")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully"),
			@ApiResponse(responseCode = "404", description = "Quiz not found") })
	ResponseEntity<LeaderboardResponse> getLeaderboardPaged(
			@Parameter(description = "Quiz ID", required = true) UUID quizId,
			@Parameter(description = "Page number (0-based)") int page, @Parameter(description = "Page size") int size);

}
