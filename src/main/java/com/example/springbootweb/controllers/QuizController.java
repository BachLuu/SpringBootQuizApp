package com.example.springbootweb.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.springbootweb.entities.dtos.quizzes.CreateQuizRequest;
import com.example.springbootweb.entities.dtos.quizzes.QuizDetailResponse;
import com.example.springbootweb.entities.dtos.quizzes.QuizSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootweb.entities.dtos.quizzes.UpdateQuizRequest;
import com.example.springbootweb.services.interfaces.IQuizService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Quiz management operations
 * Handles all CRUD operations and search functionality for quizzes
 *
 * @author SpringBootWeb Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
    private final IQuizService quizService;

    /**
     * Get all quizzes
     *
     * @return ResponseEntity containing list of all quizzes
     */
    @GetMapping
    public ResponseEntity<List<QuizSummaryResponse>> getAllQuizzes() {
        logger.info("GET /api/quizzes - Fetching all quizzes");

        List<QuizSummaryResponse> quizzes = quizService.getAllQuizzes();

        if (quizzes.isEmpty()) {
            logger.info("No quizzes found");
            return ResponseEntity.noContent().build();
        }

        logger.info("Successfully retrieved {} quizzes", quizzes.size());
        return ResponseEntity.ok(quizzes);
    }

    /**
     * Get paged quizzes
     *
     * @return ResponseEntity containing list of paged quizzes
     */
    @GetMapping("/paged")
    public ResponseEntity<Page<QuizSummaryResponse>> getPagedQuizzes(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        logger.info("GET /api/quizzes/paged - Fetching quizzes with pagination, page: {}, size: {}", page, size);

        Page<QuizSummaryResponse> quizzes = quizService.getPagedQuizzes(page, size);
        logger.info("Paged result: {} elements (page {} of {})",
                quizzes.getNumberOfElements(), quizzes.getNumber(), quizzes.getTotalPages());

        return ResponseEntity.ok(quizzes);
    }

    /**
     * Get quiz by ID with detailed information
     *
     * @param id Quiz UUID
     * @return ResponseEntity containing quiz details
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> getQuizById(@PathVariable("id") UUID id) {
        logger.info("GET /api/quizzes/{} - Fetching quiz details", id);

        QuizDetailResponse quiz = quizService.getQuizById(id);

        logger.info("Successfully retrieved quiz: {}", quiz.title());
        return ResponseEntity.ok(quiz);
    }

    /**
     * Get all active quizzes
     *
     * @return ResponseEntity containing list of active quizzes
     */
    @GetMapping("/active")
    public ResponseEntity<List<QuizSummaryResponse>> getActiveQuizzes() {
        logger.info("GET /api/quizzes/active - Fetching active quizzes");

        List<QuizSummaryResponse> quizzes = quizService.getActiveQuizzes();

        if (quizzes.isEmpty()) {
            logger.info("No active quizzes found");
            return ResponseEntity.noContent().build();
        }

        logger.info("Successfully retrieved {} active quizzes", quizzes.size());
        return ResponseEntity.ok(quizzes);
    }

    /**
     * Search quizzes by title
     *
     * @param title Search keyword
     * @return ResponseEntity containing list of matching quizzes
     */
    @GetMapping("/search")
    public ResponseEntity<List<QuizSummaryResponse>> searchByTitle(
            @RequestParam("title") String title) {
        logger.info("GET /api/quizzes/search - Searching quizzes with title: {}", title);

        List<QuizSummaryResponse> quizzes = quizService.searchByTitle(title);

        if (quizzes.isEmpty()) {
            logger.info("No quizzes found matching title: {}", title);
            return ResponseEntity.noContent().build();
        }

        logger.info("Found {} quizzes matching title: {}", quizzes.size(), title);
        return ResponseEntity.ok(quizzes);
    }

    /**
     * Get quizzes by duration range
     *
     * @param minDuration Minimum duration in minutes
     * @param maxDuration Maximum duration in minutes
     * @return ResponseEntity containing list of quizzes within duration range
     */
    @GetMapping("/duration")
    public ResponseEntity<List<QuizSummaryResponse>> getQuizzesByDurationRange(
            @RequestParam("min") int minDuration,
            @RequestParam("max") int maxDuration) {
        logger.info("GET /api/quizzes/duration - Fetching quizzes with duration range: {}-{}",
                minDuration, maxDuration);

        List<QuizSummaryResponse> quizzes = quizService.getQuizzesByDurationRange(minDuration, maxDuration);

        if (quizzes.isEmpty()) {
            logger.info("No quizzes found in duration range: {}-{}", minDuration, maxDuration);
            return ResponseEntity.noContent().build();
        }

        logger.info("Found {} quizzes in duration range", quizzes.size());
        return ResponseEntity.ok(quizzes);
    }

    /**
     * Create a new quiz
     *
     * @param createQuizRequest Quiz creation data
     * @return ResponseEntity containing created quiz
     */
    @PostMapping
    public ResponseEntity<QuizDetailResponse> createQuiz(
            @Valid @RequestBody CreateQuizRequest createQuizRequest) {
        logger.info("POST /api/quizzes - Creating new quiz: {}", createQuizRequest.title());

        QuizDetailResponse createdQuiz = quizService.createQuiz(createQuizRequest);

        logger.info("Successfully created quiz with id: {}", createdQuiz.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
    }

    /**
     * Update an existing quiz
     *
     * @param id        Quiz UUID
     * @param updateDto Quiz update data
     * @return ResponseEntity containing updated quiz
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> updateQuiz(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateQuizRequest updateDto) {
        logger.info("PUT /api/quizzes/{} - Updating quiz", id);

        QuizDetailResponse updatedQuiz = quizService.updateQuiz(id, updateDto);

        logger.info("Successfully updated quiz with id: {}", updatedQuiz.id());
        return ResponseEntity.ok(updatedQuiz);
    }

    /**
     * Delete a quiz
     *
     * @param id Quiz UUID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable("id") UUID id) {
        logger.info("DELETE /api/quizzes/{} - Deleting quiz", id);

        quizService.deleteQuiz(id);

        logger.info("Successfully deleted quiz with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get total count of quizzes
     *
     * @return ResponseEntity containing total quiz count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalQuizzes() {
        logger.info("GET /api/quizzes/count - Fetching total quiz count");

        long count = quizService.getTotalQuizzes();

        Map<String, Long> response = new HashMap<>();
        response.put("total", count);

        logger.info("Total quizzes: {}", count);
        return ResponseEntity.ok(response);
    }
}
