package com.example.springbootweb.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.springbootweb.entities.constants.ErrorMessage;
import com.example.springbootweb.entities.dtos.quizzes.CreateQuizRequest;
import com.example.springbootweb.entities.dtos.quizzes.QuizDetailResponse;
import com.example.springbootweb.entities.dtos.quizzes.QuizSummaryResponse;
import com.example.springbootweb.entities.dtos.quizzes.UpdateQuizRequest;
import com.example.springbootweb.entities.models.Quiz;
import com.example.springbootweb.exceptions.BadRequestException;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.mappers.QuizMapper;
import com.example.springbootweb.repositories.QuizRepository;
import com.example.springbootweb.services.interfaces.IQuizService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService {

    private static final String QUIZ_NOT_FOUND_WITH_ID = "Quiz not found with id: {}";
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> getAllQuizzes() {
        logger.info("Fetching all quizzes");

        List<Quiz> quizzes = quizRepository.findAll();
        logger.debug("Found {} quizzes", quizzes.size());

        return quizzes.stream()
                .map(quizMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizSummaryResponse> getPagedQuizzes(Integer page, Integer size) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;

        logger.info("Fetching paged quizzes - page: {}, size: {}", pageNumber, pageSize);

        if (pageNumber < 0) {
            throw new BadRequestException("Page must be >= 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        Page<Quiz> quizPage = quizRepository.findAll(PageRequest.of(pageNumber, pageSize));
        logger.debug("Found {} quizzes in page {}", quizPage.getNumberOfElements(), pageNumber);

        return quizPage.map(quizMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDetailResponse getQuizById(UUID id) {
        logger.info("Fetching quiz with id: {}", id);

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(QUIZ_NOT_FOUND_WITH_ID, id);
                    return new ResourceNotFoundException(ErrorMessage.QUIZ_NOT_FOUND + id);
                });

        logger.debug("Successfully found quiz: {}", quiz.getTitle());
        return quizMapper.toResponse(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> getActiveQuizzes() {
        logger.info("Fetching active quizzes");

        List<Quiz> quizzes = quizRepository.findByIsActiveTrue();
        logger.debug("Found {} active quizzes", quizzes.size());

        return quizzes.stream()
                .map(quizMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> searchByTitle(String title) {
        logger.info("Searching quizzes with title containing: {}", title);

        if (title == null || title.trim().isEmpty()) {
            logger.warn("Search title is empty");
            throw new BadRequestException("Search title cannot be empty");
        }

        List<Quiz> quizzes = quizRepository.findByTitleContainingIgnoreCase(title);
        logger.debug("Found {} quizzes matching title: {}", quizzes.size(), title);

        return quizzes.stream()
                .map(quizMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> getQuizzesByDurationRange(int minDuration, int maxDuration) {
        logger.info("Fetching quizzes with duration range: {} - {}", minDuration, maxDuration);

        if (minDuration < 0 || maxDuration < 0 || minDuration > maxDuration) {
            logger.error("Invalid duration range: {} - {}", minDuration, maxDuration);
            throw new BadRequestException("Invalid duration range");
        }

        List<Quiz> quizzes = quizRepository.findByDurationRange(minDuration, maxDuration);
        logger.debug("Found {} quizzes in duration range", quizzes.size());

        return quizzes.stream()
                .map(quizMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizDetailResponse createQuiz(CreateQuizRequest createQuizRequest) {
        logger.info("Creating new quiz with title: {}", createQuizRequest.title());

        Quiz quiz = quizMapper.toEntity(createQuizRequest);

        Quiz savedQuiz = quizRepository.save(quiz);
        logger.info("Successfully created quiz with id: {}", savedQuiz.getId());

        return quizMapper.toResponse(savedQuiz);
    }

    @Override
    @Transactional
    public QuizDetailResponse updateQuiz(UUID id, UpdateQuizRequest updateDto) {
        logger.info("Updating quiz with id: {}", id);
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(QUIZ_NOT_FOUND_WITH_ID, id);
                    return new ResourceNotFoundException(ErrorMessage.QUIZ_NOT_FOUND + id);
                });
        quizMapper.updateEntity(updateDto, existingQuiz);

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        logger.info("Successfully updated quiz with id: {}", updatedQuiz.getId());

        return quizMapper.toResponse(updatedQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(UUID id) {
        logger.info("Deleting quiz with id: {}", id);

        if (!quizRepository.existsById(id)) {
            logger.error(QUIZ_NOT_FOUND_WITH_ID, id);
            throw new ResourceNotFoundException(ErrorMessage.QUIZ_NOT_FOUND + id);
        }

        quizRepository.deleteById(id);
        logger.info("Successfully deleted quiz with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalQuizzes() {
        logger.info("Fetching total quiz count");

        long count = quizRepository.count();
        logger.debug("Total quizzes: {}", count);

        return count;
    }
}
