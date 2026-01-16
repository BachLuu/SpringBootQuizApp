package com.example.springbootweb.controllers.question.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.example.springbootweb.entities.dtos.questions.CreateQuestionRequest;
import com.example.springbootweb.entities.dtos.questions.QuestionDetailResponse;
import com.example.springbootweb.entities.dtos.questions.QuestionSummaryResponse;
import com.example.springbootweb.entities.dtos.questions.UpdateQuestionRequest;
import com.example.springbootweb.entities.enums.QuestionType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Interface for Question operations.
 * Contains all Swagger/OpenAPI documentation annotations.
 * Controller implements this interface to keep code clean.
 */
@Tag(name = "Question", description = "Question management APIs")
public interface QuestionApi {

    // ==================== READ Operations ====================

    @Operation(summary = "Get all questions", 
               description = "Retrieve a list of all questions in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved question list"),
        @ApiResponse(responseCode = "204", description = "No questions found")
    })
    ResponseEntity<List<QuestionSummaryResponse>> getAllQuestions();

    @Operation(summary = "Get paged questions", 
               description = "Retrieve questions with pagination support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paged questions")
    })
    ResponseEntity<Page<QuestionSummaryResponse>> getPagedQuestions(
            @Parameter(description = "Page number (0-based)", example = "0") Integer page,
            @Parameter(description = "Page size", example = "10") Integer size);

    @Operation(summary = "Get question by ID", 
               description = "Retrieve detailed information about a specific question")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved question details",
            content = @Content(schema = @Schema(implementation = QuestionDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Question not found")
    })
    ResponseEntity<QuestionDetailResponse> getQuestionById(
            @Parameter(description = "Question ID", required = true) UUID id);

    @Operation(summary = "Get active questions", 
               description = "Retrieve all currently active questions")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active questions"),
        @ApiResponse(responseCode = "204", description = "No active questions found")
    })
    ResponseEntity<List<QuestionSummaryResponse>> getActiveQuestions();

    @Operation(summary = "Search questions by content", 
               description = "Search for questions containing the specified content keyword")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching questions"),
        @ApiResponse(responseCode = "204", description = "No questions found matching the search criteria")
    })
    ResponseEntity<List<QuestionSummaryResponse>> searchByContent(
            @Parameter(description = "Search keyword for question content", required = true) String content);

    @Operation(summary = "Get questions by type", 
               description = "Retrieve questions filtered by question type")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved questions"),
        @ApiResponse(responseCode = "204", description = "No questions found for the specified type")
    })
    ResponseEntity<List<QuestionSummaryResponse>> getQuestionsByType(
            @Parameter(description = "Question type (SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, etc.)", required = true) QuestionType type);

    @Operation(summary = "Get total question count", 
               description = "Get the total number of questions in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved question count")
    })
    ResponseEntity<Map<String, Long>> getTotalQuestions();

    // ==================== WRITE Operations ====================

    @Operation(summary = "Create a new question", 
               description = "Create a new question with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Question created successfully",
            content = @Content(schema = @Schema(implementation = QuestionDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid question data provided")
    })
    ResponseEntity<QuestionDetailResponse> createQuestion(CreateQuestionRequest createQuestionRequest);

    @Operation(summary = "Update a question", 
               description = "Update an existing question with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Question updated successfully",
            content = @Content(schema = @Schema(implementation = QuestionDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid question data provided"),
        @ApiResponse(responseCode = "404", description = "Question not found")
    })
    ResponseEntity<QuestionDetailResponse> updateQuestion(
            @Parameter(description = "Question ID", required = true) UUID id,
            UpdateQuestionRequest updateQuestionRequest);

    @Operation(summary = "Delete a question", 
               description = "Delete a question by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Question not found")
    })
    ResponseEntity<Void> deleteQuestion(
            @Parameter(description = "Question ID", required = true) UUID id);
}
