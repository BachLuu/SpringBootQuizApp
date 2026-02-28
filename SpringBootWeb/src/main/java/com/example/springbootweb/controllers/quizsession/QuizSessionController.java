package com.example.springbootweb.controllers.quizsession;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootweb.controllers.quizsession.api.QuizSessionApi;
import com.example.springbootweb.entities.dtos.quizsessions.QuizQuestionResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionDetailResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionResultResponse;
import com.example.springbootweb.entities.dtos.quizsessions.SubmitAnswerRequest;
import com.example.springbootweb.entities.dtos.quizsessions.SubmitAnswerResponse;
import com.example.springbootweb.services.interfaces.IAuthService;
import com.example.springbootweb.services.interfaces.IQuizSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Quiz Session management.
 * Handles quiz attempts, answer submissions, and result retrieval.
 * 
 * Implements QuizSessionApi interface for clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/quiz-sessions")
@RequiredArgsConstructor
public class QuizSessionController implements QuizSessionApi {

    private static final Logger log = LoggerFactory.getLogger(QuizSessionController.class);
    
    private final IQuizSessionService quizSessionService;
    private final IAuthService authService;

    // ==================== Session Lifecycle ====================

    @Override
    @PostMapping("/start/{quizId}")
    public ResponseEntity<QuizSessionDetailResponse> startSession(
            @PathVariable UUID quizId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("POST /api/quiz-sessions/start/{}", quizId);
        UUID userId = authService.getUserIdByEmail(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizSessionService.startSession(quizId, userId));
    }

    @Override
    @GetMapping("/{sessionId}")
    public ResponseEntity<QuizSessionDetailResponse> getSession(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("GET /api/quiz-sessions/{}", sessionId);
        return ResponseEntity.ok(quizSessionService.getSession(sessionId, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    @Override
    @PutMapping("/{sessionId}/pause")
    public ResponseEntity<QuizSessionDetailResponse> pauseSession(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("PUT /api/quiz-sessions/{}/pause", sessionId);
        return ResponseEntity.ok(quizSessionService.pauseSession(sessionId, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    @Override
    @PutMapping("/{sessionId}/resume")
    public ResponseEntity<QuizSessionDetailResponse> resumeSession(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("PUT /api/quiz-sessions/{}/resume", sessionId);
        return ResponseEntity.ok(quizSessionService.resumeSession(sessionId, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    @Override
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> abandonSession(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("DELETE /api/quiz-sessions/{}", sessionId);
        quizSessionService.abandonSession(sessionId, authService.getUserIdByEmail(userDetails.getUsername()));
        return ResponseEntity.noContent().build();
    }

    // ==================== Question Navigation ====================

    @Override
    @GetMapping("/{sessionId}/current-question")
    public ResponseEntity<QuizQuestionResponse> getCurrentQuestion(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("GET /api/quiz-sessions/{}/current-question", sessionId);
        return ResponseEntity.ok(quizSessionService.getCurrentQuestion(sessionId, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    @Override
    @GetMapping("/{sessionId}/questions/{index}")
    public ResponseEntity<QuizQuestionResponse> getQuestionByIndex(
            @PathVariable UUID sessionId,
            @PathVariable int index,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("GET /api/quiz-sessions/{}/questions/{}", sessionId, index);
        return ResponseEntity.ok(quizSessionService.getQuestionByIndex(sessionId, index, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    @Override
    @PostMapping("/{sessionId}/next")
    public ResponseEntity<QuizQuestionResponse> nextQuestion(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("POST /api/quiz-sessions/{}/next", sessionId);
        return ResponseEntity.ok(quizSessionService.nextQuestion(sessionId, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    @Override
    @PostMapping("/{sessionId}/previous")
    public ResponseEntity<QuizQuestionResponse> previousQuestion(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("POST /api/quiz-sessions/{}/previous", sessionId);
        return ResponseEntity.ok(quizSessionService.previousQuestion(sessionId, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    // ==================== Answer Submission ====================

    @Override
    @PostMapping("/{sessionId}/submit-answer")
    public ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SubmitAnswerRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("POST /api/quiz-sessions/{}/submit-answer - Question: {}", sessionId, request.questionId());
        return ResponseEntity.ok(quizSessionService.submitAnswer(sessionId, request, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    // ==================== Quiz Submission & Results ====================

    @Override
    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<QuizSessionResultResponse> submitQuiz(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("POST /api/quiz-sessions/{}/submit", sessionId);
        return ResponseEntity.ok(quizSessionService.submitQuiz(sessionId, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    @Override
    @GetMapping("/{sessionId}/result")
    public ResponseEntity<QuizSessionResultResponse> getResult(
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("GET /api/quiz-sessions/{}/result", sessionId);
        return ResponseEntity.ok(quizSessionService.getResult(sessionId, authService.getUserIdByEmail(userDetails.getUsername())));
    }

    // ==================== Utility ====================

    @Override
    @GetMapping("/quiz/{quizId}/can-start")
    public ResponseEntity<Boolean> canStartSession(
            @PathVariable UUID quizId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.debug("GET /api/quiz-sessions/quiz/{}/can-start", quizId);
        return ResponseEntity.ok(quizSessionService.canStartSession(quizId, authService.getUserIdByEmail(userDetails.getUsername())));
    }
}
