package com.example.SpringBootWeb.services.interfaces;

import com.example.SpringBootWeb.entities.dtos.answers.AnswerResponseDto;
import com.example.SpringBootWeb.entities.dtos.answers.CreateAnswerDto;
import com.example.SpringBootWeb.entities.dtos.answers.UpdateAnswerDto;

import java.util.List;
import java.util.UUID;

public interface IAnswerService {
    List<AnswerResponseDto> getAllAnswers();

    AnswerResponseDto getAnswerById(UUID id);

    List<AnswerResponseDto> getActiveAnswers();

    List<AnswerResponseDto> searchByContent(String content);

    List<AnswerResponseDto> getAnswersByQuestionId(UUID questionId);

    AnswerResponseDto createAnswer(CreateAnswerDto createAnswerDto);

    AnswerResponseDto updateAnswer(UUID id, UpdateAnswerDto updateAnswerDto);

    void deleteAnswer(UUID id);

    long getTotalAnswers();
}
