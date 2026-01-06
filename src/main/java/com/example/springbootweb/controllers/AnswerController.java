package com.example.springbootweb.controllers;

import com.example.springbootweb.entities.dtos.answers.AnswerResponse;
import com.example.springbootweb.entities.dtos.answers.AnswerSummaryResponse;
import com.example.springbootweb.entities.dtos.answers.CreateAnswerRequest;
import com.example.springbootweb.entities.dtos.answers.UpdateAnswerRequest;
import com.example.springbootweb.services.interfaces.IAnswerService;
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
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private static final Logger logger = LoggerFactory.getLogger(AnswerController.class);
    private final IAnswerService answerService;

    @GetMapping
    public ResponseEntity<List<AnswerSummaryResponse>> getAllAnswers() {
        logger.info("GET /api/answers - Fetching all answers");
        List<AnswerSummaryResponse> answers = answerService.getAllAnswers();
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<AnswerSummaryResponse>> getPagedAnswers(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        logger.info("GET /api/answers/paged - Fetching answers with pagination, page: {}, size: {}", page, size);
        Page<AnswerSummaryResponse> answers = answerService.getPagedAnswers(page, size);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnswerResponse> getAnswerById(@PathVariable("id") UUID id) {
        logger.info("GET /api/answers/{} - Fetching answer details", id);
        AnswerResponse answer = answerService.getAnswerById(id);
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/active")
    public ResponseEntity<List<AnswerSummaryResponse>> getActiveAnswers() {
        logger.info("GET /api/answers/active - Fetching active answers");
        List<AnswerSummaryResponse> answers = answerService.getActiveAnswers();
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AnswerSummaryResponse>> searchByContent(@RequestParam("content") String content) {
        logger.info("GET /api/answers/search - Searching answers with content: {}", content);
        List<AnswerSummaryResponse> answers = answerService.searchByContent(content);
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<AnswerSummaryResponse>> getAnswersByQuestionId(
            @PathVariable("questionId") UUID questionId) {
        logger.info("GET /api/answers/question/{} - Fetching answers for question", questionId);
        List<AnswerSummaryResponse> answers = answerService.getAnswersByQuestionId(questionId);
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answers);
    }

    @PostMapping
    public ResponseEntity<AnswerResponse> createAnswer(@Valid @RequestBody CreateAnswerRequest createAnswerRequest) {
        logger.info("POST /api/answers - Creating new answer");
        AnswerResponse createdAnswer = answerService.createAnswer(createAnswerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAnswer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable("id") UUID id,
            @Valid @RequestBody UpdateAnswerRequest updateAnswerRequest) {
        logger.info("PUT /api/answers/{} - Updating answer", id);
        AnswerResponse updatedAnswer = answerService.updateAnswer(id, updateAnswerRequest);
        return ResponseEntity.ok(updatedAnswer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable("id") UUID id) {
        logger.info("DELETE /api/answers/{} - Deleting answer", id);
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalAnswers() {
        logger.info("GET /api/answers/count - Fetching total answer count");
        long count = answerService.getTotalAnswers();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }
}
