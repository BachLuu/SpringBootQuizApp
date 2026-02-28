package com.example.springbootweb.services.impl;

import com.example.springbootweb.entities.constants.ErrorMessage;
import com.example.springbootweb.entities.dtos.answers.AnswerFilter;
import com.example.springbootweb.entities.dtos.answers.AnswerResponse;
import com.example.springbootweb.entities.dtos.answers.AnswerSummaryResponse;
import com.example.springbootweb.entities.dtos.answers.CreateAnswerRequest;
import com.example.springbootweb.entities.dtos.answers.UpdateAnswerRequest;
import com.example.springbootweb.entities.models.Answer;
import com.example.springbootweb.entities.models.Question;
import com.example.springbootweb.exceptions.BadRequestException;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.mappers.AnswerMapper;
import com.example.springbootweb.repositories.AnswerRepository;
import com.example.springbootweb.repositories.QuestionRepository;
import com.example.springbootweb.repositories.specifications.AnswerSpecifications;
import com.example.springbootweb.services.interfaces.IAnswerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final AnswerMapper answerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AnswerSummaryResponse> getAllAnswers(AnswerFilter filter) {
        logger.info("Fetching all answers with filter: {}", filter);
        Specification<Answer> spec = AnswerSpecifications.fromFilter(filter);
        List<Answer> answers = answerRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "content"));
        return answers.stream()
                .map(answerMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnswerSummaryResponse> getPagedAnswers(Integer page, Integer size, AnswerFilter filter) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;

        logger.info("Fetching paged answers - page: {}, size: {}, filter: {}", pageNumber, pageSize, filter);

        if (pageNumber < 0) {
            throw new BadRequestException("Page must be >= 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        Specification<Answer> spec = AnswerSpecifications.fromFilter(filter);
        Page<Answer> answerPage = answerRepository.findAll(spec, PageRequest.of(pageNumber, pageSize));
        return answerPage.map(answerMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerResponse getAnswerById(UUID id) {
        logger.info("Fetching answer with id: {}", id);
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.ANSWER_NOT_FOUND + id));
        return answerMapper.toResponse(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerSummaryResponse> getActiveAnswers() {
        logger.info("Fetching active answers");
        List<Answer> answers = answerRepository.findByIsActiveTrue();
        return answers.stream()
                .map(answerMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerSummaryResponse> searchByContent(String content) {
        logger.info("Searching answers with content containing: {}", content);
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException(ErrorMessage.SEARCH_CONTENT_EMPTY);
        }
        List<Answer> answers = answerRepository.findByContentContainingIgnoreCase(content);
        return answers.stream()
                .map(answerMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerSummaryResponse> getAnswersByQuestionId(UUID questionId) {
        logger.info("Fetching answers for question id: {}", questionId);
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException(ErrorMessage.QUESTION_NOT_FOUND + questionId);
        }
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.stream()
                .map(answerMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnswerResponse createAnswer(CreateAnswerRequest createAnswerRequest) {
        logger.info("Creating new answer for question id: {}", createAnswerRequest.questionId());
        Question question = questionRepository.findById(createAnswerRequest.questionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessage.QUESTION_NOT_FOUND + createAnswerRequest.questionId()));

        Answer answer = answerMapper.toEntity(createAnswerRequest);
        answer.setQuestion(question);
        answer.setQuestionId(question.getId());

        Answer savedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(savedAnswer);
    }

    @Override
    @Transactional
    public AnswerResponse updateAnswer(UUID id, UpdateAnswerRequest updateAnswerRequest) {
        logger.info("Updating answer with id: {}", id);
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.ANSWER_NOT_FOUND + id));
        if (updateAnswerRequest.questionId() != null) {
            Question question = questionRepository.findById(updateAnswerRequest.questionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ErrorMessage.QUESTION_NOT_FOUND + updateAnswerRequest.questionId()));
            answer.setQuestion(question);
            answer.setQuestionId(question.getId());
        }

        answerMapper.updateEntity(updateAnswerRequest, answer);

        Answer updatedAnswer = answerRepository.save(answer);
        return answerMapper.toResponse(updatedAnswer);
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

}
