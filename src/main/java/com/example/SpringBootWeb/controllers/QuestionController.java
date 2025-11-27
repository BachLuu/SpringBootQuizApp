package com.example.SpringBootWeb.controllers;

import com.example.SpringBootWeb.entities.dtos.questions.CreateQuestionDto;
import com.example.SpringBootWeb.entities.dtos.questions.QuestionResponseDto;
import com.example.SpringBootWeb.entities.dtos.questions.UpdateQuestionDto;
import com.example.SpringBootWeb.entities.enums.QuestionType;
import com.example.SpringBootWeb.services.interfaces.IQuestionService;
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
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    private final IQuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestions() {
        logger.info("GET /api/questions - Fetching all questions");
        List<QuestionResponseDto> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(@PathVariable("id") UUID id) {
        logger.info("GET /api/questions/{} - Fetching question details", id);
        QuestionResponseDto question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping("/active")
    public ResponseEntity<List<QuestionResponseDto>> getActiveQuestions() {
        logger.info("GET /api/questions/active - Fetching active questions");
        List<QuestionResponseDto> questions = questionService.getActiveQuestions();
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuestionResponseDto>> searchByContent(@RequestParam("content") String content) {
        logger.info("GET /api/questions/search - Searching questions with content: {}", content);
        List<QuestionResponseDto> questions = questionService.searchByContent(content);
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByType(@PathVariable("type") QuestionType type) {
        logger.info("GET /api/questions/type/{} - Fetching questions by type", type);
        List<QuestionResponseDto> questions = questionService.getQuestionsByType(type);
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }

    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestion(@Valid @RequestBody CreateQuestionDto createQuestionDto) {
        logger.info("POST /api/questions - Creating new question");
        QuestionResponseDto createdQuestion = questionService.createQuestion(createQuestionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(@PathVariable("id") UUID id,
            @Valid @RequestBody UpdateQuestionDto updateQuestionDto) {
        logger.info("PUT /api/questions/{} - Updating question", id);
        QuestionResponseDto updatedQuestion = questionService.updateQuestion(id, updateQuestionDto);
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
