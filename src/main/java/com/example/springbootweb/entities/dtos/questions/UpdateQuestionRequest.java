package com.example.springbootweb.entities.dtos.questions;

import java.util.List;

import com.example.springbootweb.entities.dtos.questionoption.UpdateQuestionOptionRequest;
import com.example.springbootweb.entities.enums.QuestionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

/**
 * DTO cho cập nhật Question.
 *
 * @param content      Nội dung câu hỏi (optional)
 * @param questionType Loại câu hỏi (optional)
 * @param isActive     Trạng thái active (optional)
 * @param options      Danh sách các lựa chọn (optional). Nếu null thì giữ nguyên options hiện tại.
 *                     Nếu gửi list thì sẽ replace toàn bộ options cũ.
 */
public record UpdateQuestionRequest(
		@Size(min = 5, max = 5000, message = "Content must be between 5 and 5000 characters") 
		String content,

		QuestionType questionType,

		Boolean isActive,

		@Valid 
		List<UpdateQuestionOptionRequest> options) {
}
