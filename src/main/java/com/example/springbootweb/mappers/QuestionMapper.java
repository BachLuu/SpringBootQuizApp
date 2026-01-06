package com.example.springbootweb.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.springbootweb.entities.dtos.questions.CreateQuestionRequest;
import com.example.springbootweb.entities.dtos.questions.QuestionDetailResponse;
import com.example.springbootweb.entities.dtos.questions.QuestionSummaryResponse;
import com.example.springbootweb.entities.dtos.questions.UpdateQuestionRequest;
import com.example.springbootweb.entities.models.Question;

@Mapper(config = CommonMapperConfig.class)
public interface QuestionMapper {

    @Mapping(target = "id", source = "id")
    QuestionDetailResponse toResponse(Question question);

    @Mapping(target = "id", source = "id")
    QuestionSummaryResponse toSummary(Question question);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quizQuestions", ignore = true)
    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Question toEntity(CreateQuestionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quizQuestions", ignore = true)
    @Mapping(target = "answers", ignore = true)
    void updateEntity(UpdateQuestionRequest request, @MappingTarget Question question);
}
