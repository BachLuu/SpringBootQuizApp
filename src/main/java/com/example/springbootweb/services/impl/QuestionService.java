package com.example.springbootweb.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbootweb.entities.constants.ErrorMessage;
import com.example.springbootweb.entities.dtos.questions.CreateQuestionRequest;
import com.example.springbootweb.entities.dtos.questions.QuestionDetailResponse;
import com.example.springbootweb.entities.dtos.questions.QuestionSummaryResponse;
import com.example.springbootweb.entities.dtos.questions.UpdateQuestionRequest;
import com.example.springbootweb.entities.enums.QuestionType;
import com.example.springbootweb.entities.models.Question;
import com.example.springbootweb.exceptions.BadRequestException;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.mappers.QuestionMapper;
import com.example.springbootweb.repositories.QuestionRepository;
import com.example.springbootweb.services.interfaces.IQuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

	private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

	private final QuestionRepository questionRepository;

	private final QuestionMapper questionMapper;

	@Override
	@Transactional(readOnly = true)
	public List<QuestionSummaryResponse> getAllQuestions() {
		logger.info("Fetching all questions");
		List<Question> questions = questionRepository.findAll();
		return questions.stream().map(questionMapper::toSummary).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<QuestionSummaryResponse> getPagedQuestions(Integer page, Integer size) {
		int pageNumber = page == null ? 0 : page;
		int pageSize = size == null ? 10 : size;

		if (pageNumber < 0) {
			throw new BadRequestException("Page must be >= 0");
		}
		if (pageSize < 1 || pageSize > 100) {
			throw new BadRequestException("Size must be between 1 and 100");
		}

		Page<Question> questionPage = questionRepository.findAll(PageRequest.of(pageNumber, pageSize));
		return questionPage.map(questionMapper::toSummary);
	}

	@Override
	@Transactional(readOnly = true)
	public QuestionDetailResponse getQuestionById(UUID id) {
		logger.info("Fetching question with id: {}", id);
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.QUESTION_NOT_FOUND + id));
		return questionMapper.toResponse(question);
	}

	@Override
	@Transactional(readOnly = true)
	public List<QuestionSummaryResponse> getActiveQuestions() {
		logger.info("Fetching active questions");
		List<Question> questions = questionRepository.findByIsActiveTrue();
		return questions.stream().map(questionMapper::toSummary).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<QuestionSummaryResponse> searchByContent(String content) {
		logger.info("Searching questions with content containing: {}", content);
		if (content == null || content.trim().isEmpty()) {
			throw new BadRequestException(ErrorMessage.SEARCH_CONTENT_EMPTY);
		}
		List<Question> questions = questionRepository.findByContentContainingIgnoreCase(content);
		return questions.stream().map(questionMapper::toSummary).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<QuestionSummaryResponse> getQuestionsByType(QuestionType questionType) {
		logger.info("Fetching questions with type: {}", questionType);
		List<Question> questions = questionRepository.findByQuestionType(questionType);
		return questions.stream().map(questionMapper::toSummary).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public QuestionDetailResponse createQuestion(CreateQuestionRequest createQuestionRequest) {
		logger.info("Creating new question");
		Question question = questionMapper.toEntity(createQuestionRequest);
		Question savedQuestion = questionRepository.save(question);
		return questionMapper.toResponse(savedQuestion);
	}

	@Override
	@Transactional
	public QuestionDetailResponse updateQuestion(UUID id, UpdateQuestionRequest updateQuestionRequest) {
		logger.info("Updating question with id: {}", id);
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.QUESTION_NOT_FOUND + id));
		questionMapper.updateEntity(updateQuestionRequest, question);

		Question updatedQuestion = questionRepository.save(question);
		return questionMapper.toResponse(updatedQuestion);
	}

	@Override
	@Transactional
	public void deleteQuestion(UUID id) {
		logger.info("Soft deleting question with id: {}", id);
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.QUESTION_NOT_FOUND + id));

		question.setIsActive(false);
		question.getAnswers().forEach(answer -> answer.setIsActive(false));
		questionRepository.save(question);

		logger.info("Question {} marked as inactive (soft deleted)", id);
	}

	@Override
	@Transactional(readOnly = true)
	public long getTotalQuestions() {
		return questionRepository.count();
	}

}
