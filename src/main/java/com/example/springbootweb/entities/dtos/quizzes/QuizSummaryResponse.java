package com.example.springbootweb.entities.dtos.quizzes;

import java.util.UUID;

public record QuizSummaryResponse(UUID id, String title, Integer duration, String thumbnailUrl, Boolean isActive,
		String description) {
}
