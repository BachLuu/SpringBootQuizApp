package com.example.springbootweb.services.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.example.springbootweb.entities.dtos.quizzes.CreateQuizRequest;
import com.example.springbootweb.entities.dtos.quizzes.QuizDetailResponse;
import com.example.springbootweb.entities.dtos.quizzes.QuizFilter;
import com.example.springbootweb.entities.dtos.quizzes.QuizSummaryResponse;
import com.example.springbootweb.entities.dtos.quizzes.UpdateQuizRequest;

public interface IQuizService {

    List<QuizSummaryResponse> getAllQuizzes(QuizFilter filter);

    Page<QuizSummaryResponse> getPagedQuizzes(Integer page, Integer size, QuizFilter filter);

    Page<QuizDetailResponse> getPagedQuizDetail(Integer page, Integer size, QuizFilter filter);

    QuizDetailResponse getQuizById(UUID id);

    List<QuizSummaryResponse> getActiveQuizzes();

    List<QuizSummaryResponse> searchByTitle(String title);

    List<QuizSummaryResponse> getQuizzesByDurationRange(int minDuration, int maxDuration);

    QuizDetailResponse createQuiz(CreateQuizRequest createQuizRequest);

    QuizDetailResponse updateQuiz(UUID id, UpdateQuizRequest updateQuizRequest);

    void deleteQuiz(UUID id);

    long getTotalQuizzes();
}
