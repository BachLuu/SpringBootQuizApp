package com.example.SpringBootWeb.entities.dtos.questions;

import com.example.SpringBootWeb.entities.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponseDto {
    private UUID id;
    private String content;
    private QuestionType questionType;
    private Boolean isActive;
}
