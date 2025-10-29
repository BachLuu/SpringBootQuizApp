package com.example.SpringBootWeb.services;

import com.example.SpringBootWeb.dtos.UpdateQuizDto;
import com.example.SpringBootWeb.entities.Quiz;
import com.example.SpringBootWeb.repositories.QuizRepository;

import com.example.SpringBootWeb.services.interfaces.IQuizService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService implements IQuizService {

    private final QuizRepository quizRepository;

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Override
    public Quiz getQuizById(UUID id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        return quiz.orElse(null);
    }

    @Override
    public List<Quiz> getActiveQuizzes() {
        return quizRepository.findByIsActiveTrue();
    }

    @Override
    public List<Quiz> searchByTitle(String title) {
        return quizRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Quiz> getQuizzesByDurationRange(int minDuration, int maxDuration) {
        return quizRepository.findByDurationRange(minDuration, maxDuration);
    }

    @Override
    public Quiz createQuiz(Quiz quiz) {
        if (quiz.getTitle() == null || quiz.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title cannot be empty");
        }
        if (quiz.getDuration() == null || quiz.getDuration() < 1 || quiz.getDuration() > 3600) {
            throw new IllegalArgumentException("Quiz duration must be between 1 and 3600 minutes");
        }
        if (quiz.getIsActive() == null) {
            quiz.setIsActive(true);
        }
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz updateQuiz(UUID id, UpdateQuizDto updateDto) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));

        existingQuiz.setTitle(updateDto.getTitle());
        existingQuiz.setDescription(updateDto.getDescription());
        existingQuiz.setDuration(updateDto.getDuration());
        existingQuiz.setThumbnailUrl(updateDto.getThumbnailUrl());
        existingQuiz.setIsActive(updateDto.getIsActive());

        return quizRepository.save(existingQuiz);
    }

    @Override
    public void deleteQuiz(UUID id) {
        if (!quizRepository.existsById(id)) {
            throw new IllegalArgumentException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }

    @Override
    public long getTotalQuizzes() {
        return quizRepository.count();
    }
}
