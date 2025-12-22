package com.example.springbootweb.entities.dtos.questions;

import com.example.springbootweb.entities.enums.QuestionType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateQuestionDto {
    @Size(min = 5, max = 5000, message = "Content must be between 5 and 5000 characters")
    private String content;

    private QuestionType questionType;
    private Boolean isActive;
}
