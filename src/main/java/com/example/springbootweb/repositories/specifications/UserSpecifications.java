package com.example.springbootweb.repositories.specifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.springbootweb.entities.dtos.users.UserFilter;
import com.example.springbootweb.entities.models.User;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Data JPA Specifications for User entity. Provides reusable, composable query
 * predicates for dynamic filtering.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecifications {

	// ==================== Basic Field Specifications ====================

	/**
	 * Filter by keyword in firstName, lastName, or email (partial match, case-insensitive)
	 */
	public static Specification<User> keywordMatches(String keyword) {
		return (root, query, cb) -> {
			if (keyword == null || keyword.isBlank()) {
				return null;
			}
			String pattern = "%" + keyword.toLowerCase() + "%";
			return cb.or(cb.like(cb.lower(root.get("firstName")), pattern),
					cb.like(cb.lower(root.get("lastName")), pattern), cb.like(cb.lower(root.get("email")), pattern));
		};
	}

	/**
	 * Filter by active status
	 */
	public static Specification<User> isActive(Boolean isActive) {
		return (root, query, cb) -> isActive == null ? null : cb.equal(root.get("isActive"), isActive);
	}

	/**
	 * Filter users created after a specific date
	 */
	public static Specification<User> createdAfter(LocalDateTime dateTime) {
		return (root, query, cb) -> dateTime == null ? null
				: cb.greaterThanOrEqualTo(root.get("createdAt"), dateTime);
	}

	/**
	 * Filter users created before a specific date
	 */
	public static Specification<User> createdBefore(LocalDateTime dateTime) {
		return (root, query, cb) -> dateTime == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), dateTime);
	}

	// ==================== Combined Filter Specification ====================

	/**
	 * Build Specification from UserFilter object. Service calls this method - NOT
	 * Controller.
	 * @param filter the filter criteria
	 * @return combined Specification
	 */
	public static Specification<User> fromFilter(UserFilter filter) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
				String pattern = "%" + filter.getKeyword().toLowerCase() + "%";
				predicates.add(cb.or(cb.like(cb.lower(root.get("firstName")), pattern),
						cb.like(cb.lower(root.get("lastName")), pattern),
						cb.like(cb.lower(root.get("email")), pattern)));
			}

			if (filter.getIsActive() != null) {
				predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
			}

			if (filter.getCreatedAfter() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAfter()));
			}

			if (filter.getCreatedBefore() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedBefore()));
			}

			return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
