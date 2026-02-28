package com.example.springbootweb.controllers.answer.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.springbootweb.entities.dtos.answers.AnswerFilter;
import com.example.springbootweb.entities.dtos.answers.AnswerResponse;
import com.example.springbootweb.entities.dtos.answers.AnswerSummaryResponse;
import com.example.springbootweb.entities.dtos.answers.CreateAnswerRequest;
import com.example.springbootweb.entities.dtos.answers.UpdateAnswerRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Interface for Answer operations.
 * Contains all Swagger/OpenAPI documentation annotations.
 * Controller implements this interface to keep code clean.
 */
@Tag(name = "Answer", description = "Answer management APIs")
public interface AnswerApi {

    // ==================== READ Operations ====================

    @Operation(summary = "Get all answers", 
               description = "Retrieve a list of all answers in the system with optional filtering")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved answer list"),
        @ApiResponse(responseCode = "204", description = "No answers found")
    })
    ResponseEntity<List<AnswerSummaryResponse>> getAllAnswers(
            @ModelAttribute AnswerFilter filter);

    @Operation(summary = "Get paged answers", 
               description = "Retrieve answers with pagination and optional filtering support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paged answers")
    })
    ResponseEntity<Page<AnswerSummaryResponse>> getPagedAnswers(
            @Parameter(description = "Page number (0-based)", example = "0") Integer page,
            @Parameter(description = "Page size", example = "10") Integer size,
            @ModelAttribute AnswerFilter filter);

    @Operation(summary = "Get answer by ID", 
               description = "Retrieve detailed information about a specific answer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved answer details",
            content = @Content(schema = @Schema(implementation = AnswerResponse.class))),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    ResponseEntity<AnswerResponse> getAnswerById(
            @Parameter(description = "Answer ID", required = true) UUID id);

    @Operation(summary = "Get active answers", 
               description = "Retrieve all currently active answers")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active answers"),
        @ApiResponse(responseCode = "204", description = "No active answers found")
    })
    ResponseEntity<List<AnswerSummaryResponse>> getActiveAnswers();

    @Operation(summary = "Search answers by content", 
               description = "Search for answers containing the specified content keyword")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching answers"),
        @ApiResponse(responseCode = "204", description = "No answers found matching the search criteria")
    })
    ResponseEntity<List<AnswerSummaryResponse>> searchByContent(
            @Parameter(description = "Search keyword for answer content", required = true) String content);

    @Operation(summary = "Get answers by question ID", 
               description = "Retrieve all answers for a specific question")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved answers"),
        @ApiResponse(responseCode = "204", description = "No answers found for the question")
    })
    ResponseEntity<List<AnswerSummaryResponse>> getAnswersByQuestionId(
            @Parameter(description = "Question ID", required = true) UUID questionId);

    @Operation(summary = "Get total answer count", 
               description = "Get the total number of answers in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved answer count")
    })
    ResponseEntity<Map<String, Long>> getTotalAnswers();

    // ==================== WRITE Operations ====================

    @Operation(summary = "Create a new answer", 
               description = "Create a new answer with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Answer created successfully",
            content = @Content(schema = @Schema(implementation = AnswerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid answer data provided")
    })
    ResponseEntity<AnswerResponse> createAnswer(CreateAnswerRequest createAnswerRequest);

    @Operation(summary = "Update an answer", 
               description = "Update an existing answer with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Answer updated successfully",
            content = @Content(schema = @Schema(implementation = AnswerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid answer data provided"),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    ResponseEntity<AnswerResponse> updateAnswer(
            @Parameter(description = "Answer ID", required = true) UUID id,
            UpdateAnswerRequest updateAnswerRequest);

    @Operation(summary = "Delete an answer", 
               description = "Delete an answer by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Answer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    ResponseEntity<Void> deleteAnswer(
            @Parameter(description = "Answer ID", required = true) UUID id);
}
