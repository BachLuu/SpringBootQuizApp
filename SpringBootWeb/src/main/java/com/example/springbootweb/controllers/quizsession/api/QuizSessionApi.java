package com.example.springbootweb.controllers.quizsession.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.springbootweb.entities.dtos.quizsessions.QuizQuestionResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionDetailResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionResultResponse;
import com.example.springbootweb.entities.dtos.quizsessions.SubmitAnswerRequest;
import com.example.springbootweb.entities.dtos.quizsessions.SubmitAnswerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Interface for Quiz Session operations.
 * Contains all Swagger/OpenAPI documentation annotations.
 * Controller implements this interface to keep code clean.
 */
@Tag(name = "Quiz Sessions", description = "Quiz attempt and session management APIs")
public interface QuizSessionApi {

    // ==================== Session Lifecycle ====================

    @Operation(summary = "Start a new quiz session", 
               description = "Creates a new quiz session for the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Session created successfully",
            content = @Content(schema = @Schema(implementation = QuizSessionDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Quiz not active or user already has active session"),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    ResponseEntity<QuizSessionDetailResponse> startSession(
            @Parameter(description = "Quiz ID", required = true) UUID quizId,
            UserDetails userDetails);

    @Operation(summary = "Get session status", 
               description = "Get current status and progress of a quiz session")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "User doesn't own this session"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    ResponseEntity<QuizSessionDetailResponse> getSession(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    @Operation(summary = "Pause session", 
               description = "Pause an in-progress quiz session")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session paused successfully"),
        @ApiResponse(responseCode = "400", description = "Session is not in progress"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    ResponseEntity<QuizSessionDetailResponse> pauseSession(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    @Operation(summary = "Resume session", 
               description = "Resume a paused quiz session")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session resumed successfully"),
        @ApiResponse(responseCode = "400", description = "Session is not paused or has expired"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    ResponseEntity<QuizSessionDetailResponse> resumeSession(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    @Operation(summary = "Abandon session", 
               description = "Abandon/cancel a quiz session")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Session abandoned successfully"),
        @ApiResponse(responseCode = "400", description = "Session already completed"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    ResponseEntity<Void> abandonSession(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    // ==================== Question Navigation ====================

    @Operation(summary = "Get current question", 
               description = "Get the current question in the quiz session")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Question retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Session not in progress"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    ResponseEntity<QuizQuestionResponse> getCurrentQuestion(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    @Operation(summary = "Get question by index", 
               description = "Get a specific question by its index (0-based)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Question retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid index or session not in progress"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    ResponseEntity<QuizQuestionResponse> getQuestionByIndex(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            @Parameter(description = "Question index (0-based)", required = true) int index,
            UserDetails userDetails);

    @Operation(summary = "Next question", 
               description = "Navigate to the next question")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Navigated to next question"),
        @ApiResponse(responseCode = "400", description = "Already at last question or session not in progress")
    })
    ResponseEntity<QuizQuestionResponse> nextQuestion(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    @Operation(summary = "Previous question", 
               description = "Navigate to the previous question")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Navigated to previous question"),
        @ApiResponse(responseCode = "400", description = "Already at first question or session not in progress")
    })
    ResponseEntity<QuizQuestionResponse> previousQuestion(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    // ==================== Answer Submission ====================

    @Operation(summary = "Submit answer", 
               description = "Submit an answer for a question in the quiz session")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Answer submitted successfully",
            content = @Content(schema = @Schema(implementation = SubmitAnswerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid answer or session not in progress"),
        @ApiResponse(responseCode = "404", description = "Session or question not found")
    })
    ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            SubmitAnswerRequest request,
            UserDetails userDetails);

    // ==================== Quiz Submission & Results ====================

    @Operation(summary = "Submit quiz", 
               description = "Submit the entire quiz for grading and get results")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quiz submitted and graded successfully",
            content = @Content(schema = @Schema(implementation = QuizSessionResultResponse.class))),
        @ApiResponse(responseCode = "400", description = "Session already submitted or not in progress"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    ResponseEntity<QuizSessionResultResponse> submitQuiz(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    @Operation(summary = "Get result", 
               description = "Get detailed result of a completed quiz session")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Result retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Quiz not yet submitted"),
        @ApiResponse(responseCode = "404", description = "Session not found")
    })
    ResponseEntity<QuizSessionResultResponse> getResult(
            @Parameter(description = "Session ID", required = true) UUID sessionId,
            UserDetails userDetails);

    // ==================== Utility ====================

    @Operation(summary = "Check if can start", 
               description = "Check if the user can start a new session for the quiz")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Check completed successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    ResponseEntity<Boolean> canStartSession(
            @Parameter(description = "Quiz ID", required = true) UUID quizId,
            UserDetails userDetails);
}
