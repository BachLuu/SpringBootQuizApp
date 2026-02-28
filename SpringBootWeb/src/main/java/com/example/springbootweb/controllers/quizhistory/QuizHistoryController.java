package com.example.springbootweb.controllers.quizhistory;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootweb.controllers.quizhistory.api.QuizHistoryApi;
import com.example.springbootweb.entities.dtos.quizsessions.LeaderboardResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionFilter;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionSummaryResponse;
import com.example.springbootweb.services.interfaces.IAuthService;
import com.example.springbootweb.services.interfaces.IQuizSessionService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Quiz History and Leaderboard. Read-only endpoints separated from
 * session management for better organization.
 *
 * Implements QuizHistoryApi interface for clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/quiz-history")
@RequiredArgsConstructor
public class QuizHistoryController implements QuizHistoryApi {

	private static final Logger log = LoggerFactory.getLogger(QuizHistoryController.class);

	private final IQuizSessionService quizSessionService;

	private final IAuthService authService;

	// ==================== User History ====================

	@Override
	@GetMapping("/me")
	public ResponseEntity<List<QuizSessionSummaryResponse>> getMyHistory(
			@ModelAttribute QuizSessionFilter quizSessionFilter,
			@AuthenticationPrincipal UserDetails userDetails) {
		log.debug("GET /api/quiz-history/me with filters");
		UUID userId = authService.getUserIdByEmail(userDetails.getUsername());
		List<QuizSessionSummaryResponse> history = quizSessionService.getUserHistory(
				userId, quizSessionFilter);
		return history.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(history);
	}

	@Override
	@GetMapping("/me/paged")
	public ResponseEntity<Page<QuizSessionSummaryResponse>> getMyHistoryPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@ModelAttribute QuizSessionFilter quizSessionFilter,
			@AuthenticationPrincipal UserDetails userDetails) {
		log.debug("GET /api/quiz-history/me/paged - page: {}, size: {} with filters", page, size);
		UUID userId = authService.getUserIdByEmail(userDetails.getUsername());
		return ResponseEntity.ok(quizSessionService.getUserHistory(
				userId, page, size, quizSessionFilter));
	}

	@Override
	@GetMapping("/user/{userId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<QuizSessionSummaryResponse>> getUserHistory(
			@PathVariable UUID userId,
			@ModelAttribute QuizSessionFilter quizSessionFilter) {
		log.info("GET /api/quiz-history/user/{} (Admin) with filters", userId);
		List<QuizSessionSummaryResponse> history = quizSessionService.getUserHistory(
				userId, quizSessionFilter);
		return history.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(history);
	}

	// ==================== Leaderboard ====================

	@Override
	@GetMapping("/quiz/{quizId}/leaderboard")
	public ResponseEntity<LeaderboardResponse> getLeaderboard(@PathVariable UUID quizId,
			@RequestParam(defaultValue = "10") int limit) {
		log.debug("GET /api/quiz-history/quiz/{}/leaderboard - limit: {}", quizId, limit);
		return ResponseEntity.ok(quizSessionService.getLeaderboard(quizId, limit));
	}

	@Override
	@GetMapping("/quiz/{quizId}/leaderboard/paged")
	public ResponseEntity<LeaderboardResponse> getLeaderboardPaged(@PathVariable UUID quizId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		log.debug("GET /api/quiz-history/quiz/{}/leaderboard/paged - page: {}, size: {}", quizId, page, size);
		return ResponseEntity.ok(quizSessionService.getLeaderboard(quizId, page, size));
	}

}
