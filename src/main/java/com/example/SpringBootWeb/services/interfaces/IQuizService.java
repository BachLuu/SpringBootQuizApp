package com.example.SpringBootWeb.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.example.SpringBootWeb.entities.dtos.quizzes.UpdateQuizDto;
import com.example.SpringBootWeb.entities.dtos.quizzes.CreateQuizDto;
import com.example.SpringBootWeb.entities.dtos.quizzes.QuizDetailDto;
import com.example.SpringBootWeb.entities.dtos.quizzes.QuizResponseDto;

public interface IQuizService {

    List<QuizResponseDto> getAllQuizzes();

    QuizDetailDto getQuizById(UUID id);

    List<QuizResponseDto> getActiveQuizzes();

    List<QuizResponseDto> searchByTitle(String title);

    List<QuizResponseDto> getQuizzesByDurationRange(int minDuration, int maxDuration);

    QuizResponseDto createQuiz(CreateQuizDto createQuizDto);

    QuizResponseDto updateQuiz(UUID id, UpdateQuizDto updateQuizDto);

    void deleteQuiz(UUID id);

    long getTotalQuizzes();
}
