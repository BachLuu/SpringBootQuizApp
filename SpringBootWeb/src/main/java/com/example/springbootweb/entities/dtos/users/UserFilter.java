package com.example.springbootweb.entities.dtos.users;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter criteria for searching users. All fields are optional - null values are ignored
 * in the search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria for user search")
public class UserFilter {

	@Schema(description = "Search keyword for user name or email (partial match)")
	private String keyword;

	@Schema(description = "Filter by active status")
	private Boolean isActive;

	@Schema(description = "Filter users created after this date")
	private LocalDateTime createdAfter;

	@Schema(description = "Filter users created before this date")
	private LocalDateTime createdBefore;

}
