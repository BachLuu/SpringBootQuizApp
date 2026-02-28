package com.example.springbootweb.entities.dtos.roles;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter criteria for searching roles. All fields are optional - null values are ignored
 * in the search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria for role search")
public class RoleFilter {

	@Schema(description = "Search keyword for role name (partial match)")
	private String nameKeyword;

	@Schema(description = "Filter by active status")
	private Boolean isActive;

}
