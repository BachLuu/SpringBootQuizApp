package com.example.springbootweb.entities.dtos.quizzes;

import java.util.UUID;

public record QuizDetailResponse(UUID id, String title, String description, Integer duration, String thumbnailUrl,
		Boolean isActive, Integer totalQuestions, Integer totalAttempts) {
}
