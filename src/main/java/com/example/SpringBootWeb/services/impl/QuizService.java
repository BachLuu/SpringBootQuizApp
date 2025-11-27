package com.example.SpringBootWeb.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.SpringBootWeb.entities.constants.ErrorMessage;
import com.example.SpringBootWeb.entities.dtos.quizzes.CreateQuizDto;
import com.example.SpringBootWeb.entities.dtos.quizzes.QuizDetailDto;
import com.example.SpringBootWeb.entities.dtos.quizzes.QuizResponseDto;
import com.example.SpringBootWeb.exceptions.BadRequestException;
import com.example.SpringBootWeb.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SpringBootWeb.entities.dtos.quizzes.UpdateQuizDto;
import com.example.SpringBootWeb.entities.models.Quiz;
import com.example.SpringBootWeb.repositories.QuizRepository;
import com.example.SpringBootWeb.services.interfaces.IQuizService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    private final QuizRepository quizRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDto> getAllQuizzes() {
        logger.info("Fetching all quizzes");

        List<Quiz> quizzes = quizRepository.findAll();
        logger.debug("Found {} quizzes", quizzes.size());

        return quizzes.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDetailDto getQuizById(UUID id) {
        logger.info("Fetching quiz with id: {}", id);

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Quiz not found with id: {}", id);
                    return new ResourceNotFoundException(ErrorMessage.QUIZ_NOT_FOUND + id);
                });

        logger.debug("Successfully found quiz: {}", quiz.getTitle());
        return mapToDetailDto(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDto> getActiveQuizzes() {
        logger.info("Fetching active quizzes");

        List<Quiz> quizzes = quizRepository.findByIsActiveTrue();
        logger.debug("Found {} active quizzes", quizzes.size());

        return quizzes.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDto> searchByTitle(String title) {
        logger.info("Searching quizzes with title containing: {}", title);

        if (title == null || title.trim().isEmpty()) {
            logger.warn("Search title is empty");
            throw new BadRequestException("Search title cannot be empty");
        }

        List<Quiz> quizzes = quizRepository.findByTitleContainingIgnoreCase(title);
        logger.debug("Found {} quizzes matching title: {}", quizzes.size(), title);

        return quizzes.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDto> getQuizzesByDurationRange(int minDuration, int maxDuration) {
        logger.info("Fetching quizzes with duration range: {} - {}", minDuration, maxDuration);

        if (minDuration < 0 || maxDuration < 0 || minDuration > maxDuration) {
            logger.error("Invalid duration range: {} - {}", minDuration, maxDuration);
            throw new BadRequestException("Invalid duration range");
        }

        List<Quiz> quizzes = quizRepository.findByDurationRange(minDuration, maxDuration);
        logger.debug("Found {} quizzes in duration range", quizzes.size());

        return quizzes.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizResponseDto createQuiz(CreateQuizDto createQuizDto) {
        logger.info("Creating new quiz with title: {}", createQuizDto.getTitle());

        Quiz quiz = Quiz.builder()
                .title(createQuizDto.getTitle())
                .description(createQuizDto.getDescription())
                .duration(createQuizDto.getDuration())
                .thumbnailUrl(createQuizDto.getThumbnailUrl())
                .isActive(createQuizDto.getIsActive() != null ? createQuizDto.getIsActive() : true)
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        logger.info("Successfully created quiz with id: {}", savedQuiz.getId());

        return mapToResponseDto(savedQuiz);
    }

    @Override
    @Transactional
    public QuizResponseDto updateQuiz(UUID id, UpdateQuizDto updateDto) {
        logger.info("Updating quiz with id: {}", id);

        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Quiz not found with id: {}", id);
                    return new ResourceNotFoundException(ErrorMessage.QUIZ_NOT_FOUND + id);
                });

        existingQuiz.setTitle(updateDto.getTitle());
        existingQuiz.setDescription(updateDto.getDescription());
        existingQuiz.setDuration(updateDto.getDuration());
        existingQuiz.setThumbnailUrl(updateDto.getThumbnailUrl());
        existingQuiz.setIsActive(updateDto.getIsActive());

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        logger.info("Successfully updated quiz with id: {}", updatedQuiz.getId());

        return mapToResponseDto(updatedQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(UUID id) {
        logger.info("Deleting quiz with id: {}", id);

        if (!quizRepository.existsById(id)) {
            logger.error("Quiz not found with id: {}", id);
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

    // Helper methods for DTO mapping
    private QuizResponseDto mapToResponseDto(Quiz quiz) {
        return new QuizResponseDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDuration(),
                quiz.getThumbnailUrl(),
                quiz.getIsActive()
        );
    }

    private QuizDetailDto mapToDetailDto(Quiz quiz) {
        return QuizDetailDto.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .duration(quiz.getDuration())
                .thumbnailUrl(quiz.getThumbnailUrl())
                .isActive(quiz.getIsActive())
                .totalQuestions(quiz.getQuizQuestions() != null ? quiz.getQuizQuestions().size() : 0)
                .totalAttempts(quiz.getUserQuizzes() != null ? quiz.getUserQuizzes().size() : 0)
                .build();
    }
}
