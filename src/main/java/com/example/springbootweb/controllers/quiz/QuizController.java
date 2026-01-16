package com.example.springbootweb.controllers.quiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.example.springbootweb.controllers.quiz.api.QuizApi;
import com.example.springbootweb.entities.dtos.quizzes.CreateQuizRequest;
import com.example.springbootweb.entities.dtos.quizzes.QuizDetailResponse;
import com.example.springbootweb.entities.dtos.quizzes.QuizSummaryResponse;
import com.example.springbootweb.entities.dtos.quizzes.UpdateQuizRequest;
import com.example.springbootweb.services.interfaces.IQuizService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Quiz management operations.
 * Implements QuizApi interface for clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController implements QuizApi {

    private static final Logger log = LoggerFactory.getLogger(QuizController.class);
    private final IQuizService quizService;

    // ==================== READ Operations ====================

    @Override
    @GetMapping
    public ResponseEntity<List<QuizSummaryResponse>> getAllQuizzes() {
        log.info("GET /api/quizzes - Fetching all quizzes");
        List<QuizSummaryResponse> quizzes = quizService.getAllQuizzes();
        if (quizzes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quizzes);
    }

    @Override
    @GetMapping("/paged")
    public ResponseEntity<Page<QuizSummaryResponse>> getPagedQuizzes(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET /api/quizzes/paged - page: {}, size: {}", page, size);
        return ResponseEntity.ok(quizService.getPagedQuizzes(page, size));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> getQuizById(@PathVariable("id") UUID id) {
        log.info("GET /api/quizzes/{}", id);
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @Override
    @GetMapping("/active")
    public ResponseEntity<List<QuizSummaryResponse>> getActiveQuizzes() {
        log.info("GET /api/quizzes/active");
        List<QuizSummaryResponse> quizzes = quizService.getActiveQuizzes();
        if (quizzes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quizzes);
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<List<QuizSummaryResponse>> searchByTitle(
            @RequestParam("title") String title) {
        log.info("GET /api/quizzes/search - title: {}", title);
        List<QuizSummaryResponse> quizzes = quizService.searchByTitle(title);
        if (quizzes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quizzes);
    }

    @Override
    @GetMapping("/duration")
    public ResponseEntity<List<QuizSummaryResponse>> getQuizzesByDurationRange(
            @RequestParam("min") int minDuration,
            @RequestParam("max") int maxDuration) {
        log.info("GET /api/quizzes/duration - min: {}, max: {}", minDuration, maxDuration);
        List<QuizSummaryResponse> quizzes = quizService.getQuizzesByDurationRange(minDuration, maxDuration);
        if (quizzes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quizzes);
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalQuizzes() {
        log.info("GET /api/quizzes/count");
        Map<String, Long> response = new HashMap<>();
        response.put("total", quizService.getTotalQuizzes());
        return ResponseEntity.ok(response);
    }

    // ==================== WRITE Operations ====================

    @Override
    @PostMapping
    public ResponseEntity<QuizDetailResponse> createQuiz(
            @Valid @RequestBody CreateQuizRequest createQuizRequest) {
        log.info("POST /api/quizzes - Creating: {}", createQuizRequest.title());
        QuizDetailResponse created = quizService.createQuiz(createQuizRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<QuizDetailResponse> updateQuiz(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateQuizRequest updateDto) {
        log.info("PUT /api/quizzes/{}", id);
        return ResponseEntity.ok(quizService.updateQuiz(id, updateDto));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable("id") UUID id) {
        log.info("DELETE /api/quizzes/{}", id);
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
