package com.example.SpringBootWeb.services.interfaces;

import com.example.SpringBootWeb.entities.dtos.questions.CreateQuestionDto;
import com.example.SpringBootWeb.entities.dtos.questions.QuestionResponseDto;
import com.example.SpringBootWeb.entities.dtos.questions.UpdateQuestionDto;
import com.example.SpringBootWeb.entities.enums.QuestionType;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IQuestionService {
    List<QuestionResponseDto> getAllQuestions();

    Page<QuestionResponseDto> getPagedQuestions(Integer page, Integer size);

    QuestionResponseDto getQuestionById(UUID id);

    List<QuestionResponseDto> getActiveQuestions();

    List<QuestionResponseDto> searchByContent(String content);

    List<QuestionResponseDto> getQuestionsByType(QuestionType questionType);

    QuestionResponseDto createQuestion(CreateQuestionDto createQuestionDto);

    QuestionResponseDto updateQuestion(UUID id, UpdateQuestionDto updateQuestionDto);

    void deleteQuestion(UUID id);

    long getTotalQuestions();
}
