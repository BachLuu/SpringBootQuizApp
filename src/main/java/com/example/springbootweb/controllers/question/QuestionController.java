package com.example.springbootweb.controllers.question;

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

import com.example.springbootweb.controllers.question.api.QuestionApi;
import com.example.springbootweb.entities.dtos.questions.CreateQuestionRequest;
import com.example.springbootweb.entities.dtos.questions.QuestionDetailResponse;
import com.example.springbootweb.entities.dtos.questions.QuestionSummaryResponse;
import com.example.springbootweb.entities.dtos.questions.UpdateQuestionRequest;
import com.example.springbootweb.entities.enums.QuestionType;
import com.example.springbootweb.services.interfaces.IQuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Question management operations.
 * Implements QuestionApi interface for clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController implements QuestionApi {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final IQuestionService questionService;

    // ==================== READ Operations ====================

    @Override
    @GetMapping
    public ResponseEntity<List<QuestionSummaryResponse>> getAllQuestions() {
        log.info("GET /api/questions");
        List<QuestionSummaryResponse> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @Override
    @GetMapping("/paged")
    public ResponseEntity<Page<QuestionSummaryResponse>> getPagedQuestions(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET /api/questions/paged - page: {}, size: {}", page, size);
        return ResponseEntity.ok(questionService.getPagedQuestions(page, size));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDetailResponse> getQuestionById(@PathVariable("id") UUID id) {
        log.info("GET /api/questions/{}", id);
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @Override
    @GetMapping("/active")
    public ResponseEntity<List<QuestionSummaryResponse>> getActiveQuestions() {
        log.info("GET /api/questions/active");
        List<QuestionSummaryResponse> questions = questionService.getActiveQuestions();
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<List<QuestionSummaryResponse>> searchByContent(
            @RequestParam("content") String content) {
        log.info("GET /api/questions/search - content: {}", content);
        List<QuestionSummaryResponse> questions = questionService.searchByContent(content);
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @Override
    @GetMapping("/type/{type}")
    public ResponseEntity<List<QuestionSummaryResponse>> getQuestionsByType(
            @PathVariable("type") QuestionType type) {
        log.info("GET /api/questions/type/{}", type);
        List<QuestionSummaryResponse> questions = questionService.getQuestionsByType(type);
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalQuestions() {
        log.info("GET /api/questions/count");
        Map<String, Long> response = new HashMap<>();
        response.put("total", questionService.getTotalQuestions());
        return ResponseEntity.ok(response);
    }

    // ==================== WRITE Operations ====================

    @Override
    @PostMapping
    public ResponseEntity<QuestionDetailResponse> createQuestion(
            @Valid @RequestBody CreateQuestionRequest createQuestionRequest) {
        log.info("POST /api/questions");
        QuestionDetailResponse created = questionService.createQuestion(createQuestionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDetailResponse> updateQuestion(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateQuestionRequest updateQuestionRequest) {
        log.info("PUT /api/questions/{}", id);
        return ResponseEntity.ok(questionService.updateQuestion(id, updateQuestionRequest));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") UUID id) {
        log.info("DELETE /api/questions/{}", id);
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
