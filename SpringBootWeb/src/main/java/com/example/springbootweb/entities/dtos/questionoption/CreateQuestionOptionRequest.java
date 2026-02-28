package com.example.springbootweb.entities.dtos.questionoption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO cho tạo QuestionOption mới.
 *
 * @param content Nội dung của option (A, B, C, D...)
 * @param orderIndex Thứ tự hiển thị (1 -> A, 2 -> B, 3 -> C, 4 -> D)
 * @param isCorrect Đánh dấu đây có phải đáp án đúng không
 */
public record CreateQuestionOptionRequest(
		@NotBlank(message = "Option content is required") @Size(min = 1, max = 5000,
				message = "Option content must be between 1 and 5000 characters") String content,

		@NotNull(message = "Order index is required") Integer orderIndex,

		@NotNull(message = "isCorrect flag is required") Boolean isCorrect) {
}
