package com.example.SpringBootWeb.controllers;

import com.example.SpringBootWeb.entities.dtos.answers.AnswerResponseDto;
import com.example.SpringBootWeb.entities.dtos.answers.CreateAnswerDto;
import com.example.SpringBootWeb.entities.dtos.answers.UpdateAnswerDto;
import com.example.SpringBootWeb.services.interfaces.IAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public ResponseEntity<List<AnswerResponseDto>> getAllAnswers() {
        logger.info("GET /api/answers - Fetching all answers");
        List<AnswerResponseDto> answers = answerService.getAllAnswers();
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnswerResponseDto> getAnswerById(@PathVariable("id") UUID id) {
        logger.info("GET /api/answers/{} - Fetching answer details", id);
        AnswerResponseDto answer = answerService.getAnswerById(id);
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/active")
    public ResponseEntity<List<AnswerResponseDto>> getActiveAnswers() {
        logger.info("GET /api/answers/active - Fetching active answers");
        List<AnswerResponseDto> answers = answerService.getActiveAnswers();
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AnswerResponseDto>> searchByContent(@RequestParam("content") String content) {
        logger.info("GET /api/answers/search - Searching answers with content: {}", content);
        List<AnswerResponseDto> answers = answerService.searchByContent(content);
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<AnswerResponseDto>> getAnswersByQuestionId(@PathVariable("questionId") UUID questionId) {
        logger.info("GET /api/answers/question/{} - Fetching answers for question", questionId);
        List<AnswerResponseDto> answers = answerService.getAnswersByQuestionId(questionId);
        if (answers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answers);
    }

    @PostMapping
    public ResponseEntity<AnswerResponseDto> createAnswer(@Valid @RequestBody CreateAnswerDto createAnswerDto) {
        logger.info("POST /api/answers - Creating new answer");
        AnswerResponseDto createdAnswer = answerService.createAnswer(createAnswerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAnswer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnswerResponseDto> updateAnswer(@PathVariable("id") UUID id,
            @Valid @RequestBody UpdateAnswerDto updateAnswerDto) {
        logger.info("PUT /api/answers/{} - Updating answer", id);
        AnswerResponseDto updatedAnswer = answerService.updateAnswer(id, updateAnswerDto);
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
