package com.example.springbootweb.services.interfaces;

import com.example.springbootweb.entities.dtos.answers.AnswerResponse;
import com.example.springbootweb.entities.dtos.answers.AnswerSummaryResponse;
import com.example.springbootweb.entities.dtos.answers.CreateAnswerRequest;
import com.example.springbootweb.entities.dtos.answers.UpdateAnswerRequest;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IAnswerService {
    List<AnswerSummaryResponse> getAllAnswers();

    Page<AnswerSummaryResponse> getPagedAnswers(Integer page, Integer size);

    AnswerResponse getAnswerById(UUID id);

    List<AnswerSummaryResponse> getActiveAnswers();

    List<AnswerSummaryResponse> searchByContent(String content);

    List<AnswerSummaryResponse> getAnswersByQuestionId(UUID questionId);

    AnswerResponse createAnswer(CreateAnswerRequest createAnswerRequest);

    AnswerResponse updateAnswer(UUID id, UpdateAnswerRequest updateAnswerRequest);

    void deleteAnswer(UUID id);

    long getTotalAnswers();
}
