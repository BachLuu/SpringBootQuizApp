package com.example.springbootweb.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.springbootweb.entities.dtos.answers.AnswerResponse;
import com.example.springbootweb.entities.dtos.answers.AnswerSummaryResponse;
import com.example.springbootweb.entities.dtos.answers.CreateAnswerRequest;
import com.example.springbootweb.entities.dtos.answers.UpdateAnswerRequest;
import com.example.springbootweb.entities.models.Answer;

@Mapper(config = CommonMapperConfig.class)
public interface AnswerMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "isCorrect", source = "isCorrect")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "questionId", source = "questionId")
    AnswerResponse toResponse(Answer answer);

    @Mapping(target = "content", source = "content")
    @Mapping(target = "isCorrect", source = "isCorrect")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "questionId", source = "questionId")
    AnswerSummaryResponse toSummary(Answer answer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Answer toEntity(CreateAnswerRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    void updateEntity(UpdateAnswerRequest request, @MappingTarget Answer answer);
}
