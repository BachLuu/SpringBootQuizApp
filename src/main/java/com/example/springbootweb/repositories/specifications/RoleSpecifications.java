package com.example.springbootweb.repositories.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.springbootweb.entities.dtos.roles.RoleFilter;
import com.example.springbootweb.entities.models.Role;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Data JPA Specifications for Role entity. Provides reusable, composable query
 * predicates for dynamic filtering.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleSpecifications {

	// ==================== Basic Field Specifications ====================

	/**
	 * Filter by name keyword (partial match, case-insensitive)
	 */
	public static Specification<Role> nameContains(String keyword) {
		return (root, query, cb) -> keyword == null || keyword.isBlank() ? null
				: cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
	}

	/**
	 * Filter by active status
	 */
	public static Specification<Role> isActive(Boolean isActive) {
		return (root, query, cb) -> isActive == null ? null : cb.equal(root.get("isActive"), isActive);
	}

	// ==================== Combined Filter Specification ====================

	/**
	 * Build Specification from RoleFilter object. Service calls this method - NOT
	 * Controller.
	 * @param filter the filter criteria
	 * @return combined Specification
	 */
	public static Specification<Role> fromFilter(RoleFilter filter) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filter.getNameKeyword() != null && !filter.getNameKeyword().isBlank()) {
				predicates
					.add(cb.like(cb.lower(root.get("name")), "%" + filter.getNameKeyword().toLowerCase() + "%"));
			}

			if (filter.getIsActive() != null) {
				predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
			}

			return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
