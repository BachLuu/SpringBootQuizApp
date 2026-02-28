package com.example.springbootweb.entities.dtos.questions;

import java.util.List;
import java.util.UUID;

import com.example.springbootweb.entities.dtos.questionoption.QuestionOptionDetailResponse;
import com.example.springbootweb.entities.enums.QuestionType;

/**
 * DTO response chi tiết cho Question, bao gồm danh sách options.
 */
public record QuestionDetailResponse(
		UUID id, 
		String content, 
		QuestionType questionType, 
		Boolean isActive,
		List<QuestionOptionDetailResponse> options) {
}

