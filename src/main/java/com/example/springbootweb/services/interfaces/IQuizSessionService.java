package com.example.springbootweb.services.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.example.springbootweb.entities.dtos.quizsessions.LeaderboardResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizQuestionResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionFilter;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionDetailResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionResultResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionSummaryResponse;
import com.example.springbootweb.entities.dtos.quizsessions.SubmitAnswerRequest;
import com.example.springbootweb.entities.dtos.quizsessions.SubmitAnswerResponse;

/**
 * Service interface for Quiz Session management.
 * Handles quiz attempts, scoring, and result tracking.
 */
public interface IQuizSessionService {

    // ==================== Session Lifecycle ====================

    /**
     * Start a new quiz session for the current user.
     * Creates a session and marks it as IN_PROGRESS.
     *
     * @param quizId The ID of the quiz to start
     * @param userId The ID of the user starting the quiz
     * @return QuizSessionResponse with session details
     */
    QuizSessionDetailResponse startSession(UUID quizId, UUID userId);

    /**
     * Get current session status and progress.
     *
     * @param sessionId The session ID
     * @param userId The user ID (for authorization)
     * @return QuizSessionResponse with current status
     */
    QuizSessionDetailResponse getSession(UUID sessionId, UUID userId);

    /**
     * Pause an in-progress session (if allowed).
     *
     * @param sessionId The session ID
     * @param userId The user ID
     * @return QuizSessionResponse with updated status
     */
    QuizSessionDetailResponse pauseSession(UUID sessionId, UUID userId);

    /**
     * Resume a paused session.
     *
     * @param sessionId The session ID
     * @param userId The user ID
     * @return QuizSessionResponse with updated status
     */
    QuizSessionDetailResponse resumeSession(UUID sessionId, UUID userId);

    /**
     * Abandon/cancel a session.
     *
     * @param sessionId The session ID
     * @param userId The user ID
     */
    void abandonSession(UUID sessionId, UUID userId);

    // ==================== Question & Answer ====================

    /**
     * Get the current question for the session.
     *
     * @param sessionId The session ID
     * @param userId The user ID
     * @return QuizQuestionResponse with question details (without correct answer)
     */
    QuizQuestionResponse getCurrentQuestion(UUID sessionId, UUID userId);

    /**
     * Get a specific question by index.
     *
     * @param sessionId The session ID
     * @param questionIndex The 0-based question index
     * @param userId The user ID
     * @return QuizQuestionResponse with question details
     */
    QuizQuestionResponse getQuestionByIndex(UUID sessionId, int questionIndex, UUID userId);

    /**
     * Submit an answer for a question.
     *
     * @param sessionId The session ID
     * @param request The answer submission request
     * @param userId The user ID
     * @return SubmitAnswerResponse with result
     */
    SubmitAnswerResponse submitAnswer(UUID sessionId, SubmitAnswerRequest request, UUID userId);

    /**
     * Navigate to next question.
     *
     * @param sessionId The session ID
     * @param userId The user ID
     * @return QuizQuestionResponse for the next question
     */
    QuizQuestionResponse nextQuestion(UUID sessionId, UUID userId);

    /**
     * Navigate to previous question.
     *
     * @param sessionId The session ID
     * @param userId The user ID
     * @return QuizQuestionResponse for the previous question
     */
    QuizQuestionResponse previousQuestion(UUID sessionId, UUID userId);

    // ==================== Submission & Results ====================

    /**
     * Submit the entire quiz for grading.
     *
     * @param sessionId The session ID
     * @param userId The user ID
     * @return QuizSessionResultResponse with final results
     */
    QuizSessionResultResponse submitQuiz(UUID sessionId, UUID userId);

    /**
     * Get the result of a completed session.
     *
     * @param sessionId The session ID
     * @param userId The user ID
     * @return QuizSessionResultResponse with detailed results
     */
    QuizSessionResultResponse getResult(UUID sessionId, UUID userId);

    // ==================== History & Leaderboard ====================

    /**
     * Get user's quiz session history with optional filters.
     * Service builds Specification internally from filter parameters.
     *
     * @param userId The user ID
     * @param quizId Filter by quiz ID (optional)
     * @param statuses Filter by session statuses (optional)
     * @param startedAfter Filter sessions started after (optional)
     * @param startedBefore Filter sessions started before (optional)
     * @param minScore Filter by minimum score (optional)
     * @param maxScore Filter by maximum score (optional)
     * @param isPassed Filter by pass status (optional)
     * @param quizTitleKeyword Search quiz title keyword (optional)
     * @return List of quiz session summaries
     */
    List<QuizSessionSummaryResponse> getUserHistory(
            UUID userId,
            QuizSessionFilter quizSessionFilter);

    /**
     * Get user's quiz session history with pagination and optional filters.
     * Service builds Specification internally from filter parameters.
     *
     * @param userId The user ID
     * @param page Page number
     * @param size Page size
     * @param quizId Filter by quiz ID (optional)
     * @param statuses Filter by session statuses (optional)
     * @param startedAfter Filter sessions started after (optional)
     * @param startedBefore Filter sessions started before (optional)
     * @param minScore Filter by minimum score (optional)
     * @param maxScore Filter by maximum score (optional)
     * @param isPassed Filter by pass status (optional)
     * @param quizTitleKeyword Search quiz title keyword (optional)
     * @return Page of quiz session summaries
     */
    Page<QuizSessionSummaryResponse> getUserHistory(
            UUID userId,
            int page,
            int size,
            QuizSessionFilter quizSessionFilter);

    /**
     * Get leaderboard for a quiz.
     *
     * @param quizId The quiz ID
     * @param limit Maximum number of entries
     * @return LeaderboardResponse with rankings
     */
    LeaderboardResponse getLeaderboard(UUID quizId, int limit);

    /**
     * Get leaderboard with pagination.
     *
     * @param quizId The quiz ID
     * @param page Page number
     * @param size Page size
     * @return LeaderboardResponse with rankings
     */
    LeaderboardResponse getLeaderboard(UUID quizId, int page, int size);

    // ==================== Admin/System Operations ====================

    /**
     * Process expired sessions (auto-submit).
     * Called by scheduled job.
     */
    void processExpiredSessions();

    /**
     * Check if user can start a new session for a quiz.
     *
     * @param quizId The quiz ID
     * @param userId The user ID
     * @return true if user can start a new session
     */
    boolean canStartSession(UUID quizId, UUID userId);
}
