package com.example.springbootweb.entities.dtos.questions;

import java.util.Set;

import com.example.springbootweb.entities.enums.QuestionType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter criteria for searching questions. All fields are optional - null values are
 * ignored in the search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria for question search")
public class QuestionFilter {

	@Schema(description = "Search keyword for question content (partial match)")
	private String contentKeyword;

	@Schema(description = "Filter by question types (multiple allowed)")
	private Set<QuestionType> questionTypes;

	@Schema(description = "Filter by active status")
	private Boolean isActive;

}
