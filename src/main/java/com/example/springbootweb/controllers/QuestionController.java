package com.example.springbootweb.controllers;

import com.example.springbootweb.entities.dtos.questions.CreateQuestionRequest;
import com.example.springbootweb.entities.dtos.questions.QuestionDetailResponse;
import com.example.springbootweb.entities.dtos.questions.QuestionSummaryResponse;
import com.example.springbootweb.entities.dtos.questions.UpdateQuestionRequest;
import com.example.springbootweb.entities.enums.QuestionType;
import com.example.springbootweb.services.interfaces.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    private final IQuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionSummaryResponse>> getAllQuestions() {
        logger.info("GET /api/questions - Fetching all questions");
        List<QuestionSummaryResponse> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<QuestionSummaryResponse>> getPagedQuestions(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        logger.info(
                "GET /api/questions/paged - Fetching questions with pagination, page: {}, size: {}",
                page, size);
        Page<QuestionSummaryResponse> questions = questionService.getPagedQuestions(page, size);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDetailResponse> getQuestionById(@PathVariable("id") UUID id) {
        logger.info("GET /api/questions/{} - Fetching question details", id);
        QuestionDetailResponse question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping("/active")
    public ResponseEntity<List<QuestionSummaryResponse>> getActiveQuestions() {
        logger.info("GET /api/questions/active - Fetching active questions");
        List<QuestionSummaryResponse> questions = questionService.getActiveQuestions();
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuestionSummaryResponse>> searchByContent(
            @RequestParam("content") String content) {
        logger.info("GET /api/questions/search - Searching questions with content: {}", content);
        List<QuestionSummaryResponse> questions = questionService.searchByContent(content);
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<QuestionSummaryResponse>> getQuestionsByType(
            @PathVariable("type") QuestionType type) {
        logger.info("GET /api/questions/type/{} - Fetching questions by type", type);
        List<QuestionSummaryResponse> questions = questionService.getQuestionsByType(type);
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @PostMapping
    public ResponseEntity<QuestionDetailResponse> createQuestion(
            @Valid @RequestBody CreateQuestionRequest createQuestionRequest) {
        logger.info("POST /api/questions - Creating new question");
        QuestionDetailResponse createdQuestion = questionService.createQuestion(createQuestionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDetailResponse> updateQuestion(@PathVariable("id") UUID id,
            @Valid @RequestBody UpdateQuestionRequest updateQuestionRequest) {
        logger.info("PUT /api/questions/{} - Updating question", id);
        QuestionDetailResponse updatedQuestion =
                questionService.updateQuestion(id, updateQuestionRequest);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") UUID id) {
        logger.info("DELETE /api/questions/{} - Deleting question", id);
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalQuestions() {
        logger.info("GET /api/questions/count - Fetching total question count");
        long count = questionService.getTotalQuestions();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }
}
