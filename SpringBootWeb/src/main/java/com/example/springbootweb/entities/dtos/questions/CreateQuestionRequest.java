package com.example.springbootweb.entities.dtos.questions;

import java.util.List;

import com.example.springbootweb.entities.dtos.questionoption.CreateQuestionOptionRequest;
import com.example.springbootweb.entities.enums.QuestionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO cho tạo Question mới.
 *
 * @param content Nội dung câu hỏi
 * @param questionType Loại câu hỏi (SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE,
 * LONG_ANSWER)
 * @param options Danh sách các lựa chọn (chỉ dùng cho SINGLE_CHOICE, MULTIPLE_CHOICE). Có
 * thể null hoặc empty cho TRUE_FALSE, LONG_ANSWER.
 */
public record CreateQuestionRequest(
		@NotBlank(message = "Content is required") @Size(min = 5, max = 5000,
				message = "Content must be between 5 and 5000 characters") String content,

		@NotNull(message = "Question type is required") QuestionType questionType,

		@Valid List<CreateQuestionOptionRequest> options) {
}
