package com.example.springbootweb.entities.dtos.questionoption;

import java.util.UUID;

/**
 * DTO response chi tiết cho QuestionOption.
 *
 * @param id         UUID của option
 * @param content    Nội dung của option
 * @param orderIndex Thứ tự hiển thị (1 -> A, 2 -> B, 3 -> C, 4 -> D)
 * @param isCorrect  Đáp án đúng hay không (chỉ trả về cho Admin, không trả về cho user đang làm bài)
 * @param isActive   Trạng thái active
 */
public record QuestionOptionDetailResponse(
		UUID id, 
		String content, 
		Integer orderIndex, 
		Boolean isCorrect,
		Boolean isActive) {
}
