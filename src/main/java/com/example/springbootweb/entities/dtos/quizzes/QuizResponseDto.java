package com.example.springbootweb.entities.dtos.quizzes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDto {
    private UUID id;
    private String title;
    private String description;
    private Integer duration;
    private String thumbnailUrl;
    private Boolean isActive;
}