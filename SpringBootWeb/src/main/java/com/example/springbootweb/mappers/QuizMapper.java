package com.example.springbootweb.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.springbootweb.entities.dtos.quizzes.CreateQuizRequest;
import com.example.springbootweb.entities.dtos.quizzes.QuizDetailResponse;
import com.example.springbootweb.entities.dtos.quizzes.QuizSummaryResponse;
import com.example.springbootweb.entities.dtos.quizzes.UpdateQuizRequest;
import com.example.springbootweb.entities.models.Quiz;

@Mapper(config = CommonMapperConfig.class)
public interface QuizMapper {

	@Mapping(target = "totalQuestions",
			expression = "java(quiz.getQuizQuestions() == null ? 0 : quiz.getQuizQuestions().size())")
	@Mapping(target = "totalAttempts",
			expression = "java(quiz.getUserQuizzes() == null ? 0 : quiz.getUserQuizzes().size())")
	QuizDetailResponse toResponse(Quiz quiz);

	QuizSummaryResponse toSummary(Quiz quiz);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userQuizzes", ignore = true)
	@Mapping(target = "quizQuestions", ignore = true)
	@Mapping(target = "isActive", expression = "java(request.isActive() == null ? Boolean.TRUE : request.isActive())")
	Quiz toEntity(CreateQuizRequest request);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userQuizzes", ignore = true)
	@Mapping(target = "quizQuestions", ignore = true)
	void updateEntity(UpdateQuizRequest request, @MappingTarget Quiz quiz);

}
