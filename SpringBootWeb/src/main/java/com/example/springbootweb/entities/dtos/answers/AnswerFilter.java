package com.example.springbootweb.entities.dtos.answers;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter criteria for searching answers. All fields are optional - null values are ignored
 * in the search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria for answer search")
public class AnswerFilter {

	@Schema(description = "Search keyword for answer content (partial match)")
	private String contentKeyword;

	@Schema(description = "Filter by question ID")
	private UUID questionId;

	@Schema(description = "Filter by correct status (true = correct answers only)")
	private Boolean isCorrect;

	@Schema(description = "Filter by active status")
	private Boolean isActive;

}
