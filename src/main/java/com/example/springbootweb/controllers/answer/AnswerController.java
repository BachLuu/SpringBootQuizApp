package com.example.springbootweb.controllers.answer;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootweb.controllers.answer.api.AnswerApi;
import com.example.springbootweb.entities.dtos.answers.AnswerFilter;
import com.example.springbootweb.entities.dtos.answers.AnswerResponse;
import com.example.springbootweb.entities.dtos.answers.AnswerSummaryResponse;
import com.example.springbootweb.entities.dtos.answers.CreateAnswerRequest;
import com.example.springbootweb.entities.dtos.answers.UpdateAnswerRequest;
import com.example.springbootweb.services.interfaces.IAnswerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Answer management operations. Implements AnswerApi interface for
 * clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController implements AnswerApi {

	private static final Logger log = LoggerFactory.getLogger(AnswerController.class);

	private final IAnswerService answerService;

	// ==================== READ Operations ====================

	@Override
	@GetMapping
	public ResponseEntity<List<AnswerSummaryResponse>> getAllAnswers(
			@ModelAttribute AnswerFilter filter) {
		log.info("GET /api/answers with filter: {}", filter);
		List<AnswerSummaryResponse> answers = answerService.getAllAnswers(filter);
		if (answers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(answers);
	}

	@Override
	@GetMapping("/paged")
	public ResponseEntity<Page<AnswerSummaryResponse>> getPagedAnswers(
			@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "size", defaultValue = "10") Integer size,
			@ModelAttribute AnswerFilter filter) {
		log.info("GET /api/answers/paged - page: {}, size: {}, filter: {}", page, size, filter);
		return ResponseEntity.ok(answerService.getPagedAnswers(page, size, filter));
	}

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<AnswerResponse> getAnswerById(@PathVariable("id") UUID id) {
		log.info("GET /api/answers/{}", id);
		return ResponseEntity.ok(answerService.getAnswerById(id));
	}

	@Override
	@GetMapping("/active")
	public ResponseEntity<List<AnswerSummaryResponse>> getActiveAnswers() {
		log.info("GET /api/answers/active");
		List<AnswerSummaryResponse> answers = answerService.getActiveAnswers();
		if (answers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(answers);
	}

	@Override
	@GetMapping("/search")
	public ResponseEntity<List<AnswerSummaryResponse>> searchByContent(@RequestParam("content") String content) {
		log.info("GET /api/answers/search - content: {}", content);
		List<AnswerSummaryResponse> answers = answerService.searchByContent(content);
		if (answers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(answers);
	}

	@Override
	@GetMapping("/question/{questionId}")
	public ResponseEntity<List<AnswerSummaryResponse>> getAnswersByQuestionId(
			@PathVariable("questionId") UUID questionId) {
		log.info("GET /api/answers/question/{}", questionId);
		List<AnswerSummaryResponse> answers = answerService.getAnswersByQuestionId(questionId);
		if (answers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(answers);
	}

	@Override
	@GetMapping("/count")
	public ResponseEntity<Map<String, Long>> getTotalAnswers() {
		log.info("GET /api/answers/count");
		Map<String, Long> response = new HashMap<>();
		response.put("total", answerService.getTotalAnswers());
		return ResponseEntity.ok(response);
	}

	// ==================== WRITE Operations ====================

	@Override
	@PostMapping
	public ResponseEntity<AnswerResponse> createAnswer(@Valid @RequestBody CreateAnswerRequest createAnswerRequest) {
		log.info("POST /api/answers");
		AnswerResponse created = answerService.createAnswer(createAnswerRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@Override
	@PutMapping("/{id}")
	public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable("id") UUID id,
			@Valid @RequestBody UpdateAnswerRequest updateAnswerRequest) {
		log.info("PUT /api/answers/{}", id);
		return ResponseEntity.ok(answerService.updateAnswer(id, updateAnswerRequest));
	}

	@Override
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAnswer(@PathVariable("id") UUID id) {
		log.info("DELETE /api/answers/{}", id);
		answerService.deleteAnswer(id);
		return ResponseEntity.noContent().build();
	}

}
