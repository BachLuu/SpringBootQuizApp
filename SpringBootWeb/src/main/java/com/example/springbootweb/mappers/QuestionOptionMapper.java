package com.example.springbootweb.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.springbootweb.entities.dtos.questionoption.CreateQuestionOptionRequest;
import com.example.springbootweb.entities.dtos.questionoption.QuestionOptionDetailResponse;
import com.example.springbootweb.entities.dtos.questionoption.UpdateQuestionOptionRequest;
import com.example.springbootweb.entities.models.QuestionOption;

@Mapper(config = CommonMapperConfig.class)
public interface QuestionOptionMapper {

	QuestionOptionDetailResponse toResponse(QuestionOption option);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "questionId", ignore = true)
	@Mapping(target = "question", ignore = true)
	@Mapping(target = "isActive", constant = "true")
	QuestionOption toEntityFromCreate(CreateQuestionOptionRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "questionId", ignore = true)
	@Mapping(target = "question", ignore = true)
	void updateEntity(UpdateQuestionOptionRequest request, @MappingTarget QuestionOption option);

	/**
	 * Tạo entity mới từ UpdateQuestionOptionRequest (dùng khi option chưa tồn tại trong
	 * update flow).
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "questionId", ignore = true)
	@Mapping(target = "question", ignore = true)
	QuestionOption toEntityFromUpdate(UpdateQuestionOptionRequest request);

}
