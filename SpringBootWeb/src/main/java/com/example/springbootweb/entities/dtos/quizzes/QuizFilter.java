package com.example.springbootweb.entities.dtos.quizzes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter criteria for searching quizzes. All fields are optional - null values are
 * ignored in the search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria for quiz search")
public class QuizFilter {

	@Schema(description = "Search keyword for quiz title (partial match)")
	private String titleKeyword;

	@Schema(description = "Filter by active status")
	private Boolean isActive;

	@Schema(description = "Filter by minimum duration (in minutes)")
	private Integer minDuration;

	@Schema(description = "Filter by maximum duration (in minutes)")
	private Integer maxDuration;

}
