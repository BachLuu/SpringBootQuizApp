package com.example.springbootweb.services.interfaces;

import com.example.springbootweb.entities.dtos.questions.CreateQuestionRequest;
import com.example.springbootweb.entities.dtos.questions.QuestionDetailResponse;
import com.example.springbootweb.entities.dtos.questions.QuestionSummaryResponse;
import com.example.springbootweb.entities.dtos.questions.UpdateQuestionRequest;
import com.example.springbootweb.entities.enums.QuestionType;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IQuestionService {
    List<QuestionSummaryResponse> getAllQuestions();

    Page<QuestionSummaryResponse> getPagedQuestions(Integer page, Integer size);

    QuestionDetailResponse getQuestionById(UUID id);

    List<QuestionSummaryResponse> getActiveQuestions();

    List<QuestionSummaryResponse> searchByContent(String content);

    List<QuestionSummaryResponse> getQuestionsByType(QuestionType questionType);

    QuestionDetailResponse createQuestion(CreateQuestionRequest createQuestionRequest);

    QuestionDetailResponse updateQuestion(UUID id, UpdateQuestionRequest updateQuestionRequest);

    void deleteQuestion(UUID id);

    long getTotalQuestions();
}
