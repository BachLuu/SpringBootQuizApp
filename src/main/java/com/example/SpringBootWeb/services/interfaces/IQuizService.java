package com.example.SpringBootWeb.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.example.SpringBootWeb.dtos.UpdateQuizDto;
import com.example.SpringBootWeb.entities.Quiz;

public interface IQuizService {

    List<Quiz> getAllQuizzes();

    Quiz getQuizById(UUID id);

    List<Quiz> getActiveQuizzes();

    List<Quiz> searchByTitle(String title);

    List<Quiz> getQuizzesByDurationRange(int minDuration, int maxDuration);

    Quiz createQuiz(Quiz quiz);

    Quiz updateQuiz(UUID id, UpdateQuizDto updateQuizDto);

    void deleteQuiz(UUID id);

    long getTotalQuizzes();
}
