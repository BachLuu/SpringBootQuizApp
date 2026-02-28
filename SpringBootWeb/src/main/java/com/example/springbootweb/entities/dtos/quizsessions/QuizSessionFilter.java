package com.example.springbootweb.entities.dtos.quizsessions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.example.springbootweb.entities.enums.QuizSessionStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter criteria for searching quiz sessions. All fields are optional - null values are
 * ignored in the search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria for quiz session search")
public class QuizSessionFilter {

	@Schema(description = "Filter by user ID")
	private UUID userId;

	@Schema(description = "Filter by quiz ID")
	private UUID quizId;

	@Schema(description = "Filter by session statuses (multiple allowed)")
	private Set<QuizSessionStatus> statuses;

	@Schema(description = "Filter sessions started after this date")
	private LocalDateTime startedAfter;

	@Schema(description = "Filter sessions started before this date")
	private LocalDateTime startedBefore;

	@Schema(description = "Filter sessions finished after this date")
	private LocalDateTime finishedAfter;

	@Schema(description = "Filter sessions finished before this date")
	private LocalDateTime finishedBefore;

	@Schema(description = "Filter by minimum score")
	private BigDecimal minScore;

	@Schema(description = "Filter by maximum score")
	private BigDecimal maxScore;

	@Schema(description = "Filter by pass status (true = passed, false = failed)")
	private Boolean isPassed;

	@Schema(description = "Search keyword for quiz title (partial match)")
	private String quizTitleKeyword;

}
