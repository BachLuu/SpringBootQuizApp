package com.example.SpringBootWeb.services.impl;

import com.example.SpringBootWeb.entities.constants.ErrorMessage;
import com.example.SpringBootWeb.entities.dtos.questions.CreateQuestionDto;
import com.example.SpringBootWeb.entities.dtos.questions.QuestionResponseDto;
import com.example.SpringBootWeb.entities.dtos.questions.UpdateQuestionDto;
import com.example.SpringBootWeb.entities.enums.QuestionType;
import com.example.SpringBootWeb.entities.models.Question;
import com.example.SpringBootWeb.exceptions.BadRequestException;
import com.example.SpringBootWeb.exceptions.ResourceNotFoundException;
import com.example.SpringBootWeb.repositories.QuestionRepository;
import com.example.SpringBootWeb.services.interfaces.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);
    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getAllQuestions() {
        logger.info("Fetching all questions");
        List<Question> questions = questionRepository.findAll();
        return questions.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDto> getPagedQuestions(Integer page, Integer size) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;

        if (pageNumber < 0) {
            throw new BadRequestException("Page must be >= 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        Page<Question> questionPage = questionRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return questionPage.map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto getQuestionById(UUID id) {
        logger.info("Fetching question with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.QUESTION_NOT_FOUND + id));
        return mapToResponseDto(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getActiveQuestions() {
        logger.info("Fetching active questions");
        List<Question> questions = questionRepository.findByIsActiveTrue();
        return questions.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> searchByContent(String content) {
        logger.info("Searching questions with content containing: {}", content);
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException(ErrorMessage.SEARCH_CONTENT_EMPTY);
        }
        List<Question> questions = questionRepository.findByContentContainingIgnoreCase(content);
        return questions.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByType(QuestionType questionType) {
        logger.info("Fetching questions with type: {}", questionType);
        List<Question> questions = questionRepository.findByQuestionType(questionType);
        return questions.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuestionResponseDto createQuestion(CreateQuestionDto createQuestionDto) {
        logger.info("Creating new question");
        Question question = Question.builder()
                .content(createQuestionDto.getContent())
                .questionType(createQuestionDto.getQuestionType())
                .isActive(true)
                .build();

        Question savedQuestion = questionRepository.save(question);
        return mapToResponseDto(savedQuestion);
    }

    @Override
    @Transactional
    public QuestionResponseDto updateQuestion(UUID id, UpdateQuestionDto updateQuestionDto) {
        logger.info("Updating question with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.QUESTION_NOT_FOUND + id));

        if (updateQuestionDto.getContent() != null) {
            question.setContent(updateQuestionDto.getContent());
        }
        if (updateQuestionDto.getQuestionType() != null) {
            question.setQuestionType(updateQuestionDto.getQuestionType());
        }
        if (updateQuestionDto.getIsActive() != null) {
            question.setIsActive(updateQuestionDto.getIsActive());
        }

        Question updatedQuestion = questionRepository.save(question);
        return mapToResponseDto(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(UUID id) {
        logger.info("Deleting question with id: {}", id);
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessage.QUESTION_NOT_FOUND + id);
        }
        questionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalQuestions() {
        return questionRepository.count();
    }

    private QuestionResponseDto mapToResponseDto(Question question) {
        return QuestionResponseDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .questionType(question.getQuestionType())
                .isActive(question.getIsActive())
                .build();
    }
}
