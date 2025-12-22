package com.example.springbootweb.services.impl;

import com.example.springbootweb.entities.constants.ErrorMessage;
import com.example.springbootweb.entities.dtos.answers.AnswerResponseDto;
import com.example.springbootweb.entities.dtos.answers.CreateAnswerDto;
import com.example.springbootweb.entities.dtos.answers.UpdateAnswerDto;
import com.example.springbootweb.entities.models.Answer;
import com.example.springbootweb.entities.models.Question;
import com.example.springbootweb.exceptions.BadRequestException;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.repositories.AnswerRepository;
import com.example.springbootweb.repositories.QuestionRepository;
import com.example.springbootweb.services.interfaces.IAnswerService;
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
public class AnswerService implements IAnswerService {

    private static final Logger logger = LoggerFactory.getLogger(AnswerService.class);
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponseDto> getAllAnswers() {
        logger.info("Fetching all answers");
        List<Answer> answers = answerRepository.findAll();
        return answers.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnswerResponseDto> getPagedAnswers(Integer page, Integer size) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;

        if (pageNumber < 0) {
            throw new BadRequestException("Page must be >= 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        Page<Answer> answerPage = answerRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return answerPage.map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerResponseDto getAnswerById(UUID id) {
        logger.info("Fetching answer with id: {}", id);
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.ANSWER_NOT_FOUND + id));
        return mapToResponseDto(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponseDto> getActiveAnswers() {
        logger.info("Fetching active answers");
        List<Answer> answers = answerRepository.findByIsActiveTrue();
        return answers.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponseDto> searchByContent(String content) {
        logger.info("Searching answers with content containing: {}", content);
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException(ErrorMessage.SEARCH_CONTENT_EMPTY);
        }
        List<Answer> answers = answerRepository.findByContentContainingIgnoreCase(content);
        return answers.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerResponseDto> getAnswersByQuestionId(UUID questionId) {
        logger.info("Fetching answers for question id: {}", questionId);
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException(ErrorMessage.QUESTION_NOT_FOUND + questionId);
        }
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnswerResponseDto createAnswer(CreateAnswerDto createAnswerDto) {
        logger.info("Creating new answer for question id: {}", createAnswerDto.getQuestionId());
        Question question = questionRepository.findById(createAnswerDto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.QUESTION_NOT_FOUND + createAnswerDto.getQuestionId()));

        Answer answer = Answer.builder()
                .content(createAnswerDto.getContent())
                .isCorrect(createAnswerDto.getIsCorrect())
                .questionId(createAnswerDto.getQuestionId())
                .question(question)
                .isActive(true)
                .build();

        Answer savedAnswer = answerRepository.save(answer);
        return mapToResponseDto(savedAnswer);
    }

    @Override
    @Transactional
    public AnswerResponseDto updateAnswer(UUID id, UpdateAnswerDto updateAnswerDto) {
        logger.info("Updating answer with id: {}", id);
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.ANSWER_NOT_FOUND + id));

        if (updateAnswerDto.getContent() != null) {
            answer.setContent(updateAnswerDto.getContent());
        }
        if (updateAnswerDto.getIsCorrect() != null) {
            answer.setIsCorrect(updateAnswerDto.getIsCorrect());
        }
        if (updateAnswerDto.getIsActive() != null) {
            answer.setIsActive(updateAnswerDto.getIsActive());
        }
        if (updateAnswerDto.getQuestionId() != null) {
            Question question = questionRepository.findById(updateAnswerDto.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ErrorMessage.QUESTION_NOT_FOUND + updateAnswerDto.getQuestionId()));
            answer.setQuestionId(updateAnswerDto.getQuestionId());
            answer.setQuestion(question);
        }

        Answer updatedAnswer = answerRepository.save(answer);
        return mapToResponseDto(updatedAnswer);
    }

    @Override
    @Transactional
    public void deleteAnswer(UUID id) {
        logger.info("Deleting answer with id: {}", id);
        if (!answerRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorMessage.ANSWER_NOT_FOUND + id);
        }
        answerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalAnswers() {
        return answerRepository.count();
    }

    private AnswerResponseDto mapToResponseDto(Answer answer) {
        return AnswerResponseDto.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .isCorrect(answer.getIsCorrect())
                .isActive(answer.getIsActive())
                .questionId(answer.getQuestionId())
                .build();
    }
}
