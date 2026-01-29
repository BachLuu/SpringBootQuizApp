package com.example.springbootweb.controllers.quiz.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.springbootweb.entities.dtos.quizzes.CreateQuizRequest;
import com.example.springbootweb.entities.dtos.quizzes.QuizDetailResponse;
import com.example.springbootweb.entities.dtos.quizzes.QuizFilter;
import com.example.springbootweb.entities.dtos.quizzes.QuizSummaryResponse;
import com.example.springbootweb.entities.dtos.quizzes.UpdateQuizRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Interface for Quiz operations.
 * Contains all Swagger/OpenAPI documentation annotations.
 * Controller implements this interface to keep code clean.
 */
@Tag(name = "Quiz", description = "Quiz management APIs")
public interface QuizApi {

    // ==================== READ Operations ====================

    @Operation(summary = "Get all quizzes", 
               description = "Retrieve a list of all quizzes in the system with optional filtering")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz list"),
        @ApiResponse(responseCode = "204", description = "No quizzes found")
    })
    ResponseEntity<List<QuizSummaryResponse>> getAllQuizzes(
            @ModelAttribute QuizFilter filter);

    @Operation(summary = "Get paged quizzes", 
               description = "Retrieve quizzes with pagination and optional filtering support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paged quizzes")
    })
    ResponseEntity<Page<QuizSummaryResponse>> getPagedQuizzes(
            @Parameter(description = "Page number (0-based)", example = "0") Integer page,
            @Parameter(description = "Page size", example = "10") Integer size,
            @ModelAttribute QuizFilter filter);

    @Operation(summary = "Get paged quiz details", 
               description = "Retrieve detailed quiz information with pagination and optional filtering support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paged quiz details",
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<Page<QuizDetailResponse>> getPagedQuizDetail(
            @Parameter(description = "Page number (0-based)", example = "0") Integer page,
            @Parameter(description = "Page size", example = "10") Integer size,
            @ModelAttribute QuizFilter filter);

    @Operation(summary = "Get quiz by ID", 
               description = "Retrieve detailed information about a specific quiz")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz details",
            content = @Content(schema = @Schema(implementation = QuizDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    ResponseEntity<QuizDetailResponse> getQuizById(
            @Parameter(description = "Quiz ID", required = true) UUID id);

    @Operation(summary = "Get active quizzes", 
               description = "Retrieve all currently active quizzes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active quizzes"),
        @ApiResponse(responseCode = "204", description = "No active quizzes found")
    })
    ResponseEntity<List<QuizSummaryResponse>> getActiveQuizzes();

    @Operation(summary = "Search quizzes by title", 
               description = "Search for quizzes containing the specified title keyword")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching quizzes"),
        @ApiResponse(responseCode = "204", description = "No quizzes found matching the search criteria")
    })
    ResponseEntity<List<QuizSummaryResponse>> searchByTitle(
            @Parameter(description = "Search keyword for quiz title", required = true) String title);

    @Operation(summary = "Get quizzes by duration range", 
               description = "Retrieve quizzes within a specified duration range (in minutes)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quizzes"),
        @ApiResponse(responseCode = "204", description = "No quizzes found in the specified range")
    })
    ResponseEntity<List<QuizSummaryResponse>> getQuizzesByDurationRange(
            @Parameter(description = "Minimum duration in minutes", required = true) int minDuration,
            @Parameter(description = "Maximum duration in minutes", required = true) int maxDuration);

    @Operation(summary = "Get total quiz count", 
               description = "Get the total number of quizzes in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz count")
    })
    ResponseEntity<Map<String, Long>> getTotalQuizzes();

    // ==================== WRITE Operations ====================

    @Operation(summary = "Create a new quiz", 
               description = "Create a new quiz with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Quiz created successfully",
            content = @Content(schema = @Schema(implementation = QuizDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid quiz data provided")
    })
    ResponseEntity<QuizDetailResponse> createQuiz(CreateQuizRequest createQuizRequest);

    @Operation(summary = "Update a quiz", 
               description = "Update an existing quiz with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quiz updated successfully",
            content = @Content(schema = @Schema(implementation = QuizDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid quiz data provided"),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    ResponseEntity<QuizDetailResponse> updateQuiz(
            @Parameter(description = "Quiz ID", required = true) UUID id,
            UpdateQuizRequest updateDto);

    @Operation(summary = "Delete a quiz", 
               description = "Delete a quiz by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Quiz deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    ResponseEntity<Void> deleteQuiz(
            @Parameter(description = "Quiz ID", required = true) UUID id);
}
