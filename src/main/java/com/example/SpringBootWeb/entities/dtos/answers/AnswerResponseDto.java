package com.example.SpringBootWeb.entities.dtos.answers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerResponseDto {
    private UUID id;
    private String content;
    private Boolean isCorrect;
    private Boolean isActive;
    private UUID questionId;
}
