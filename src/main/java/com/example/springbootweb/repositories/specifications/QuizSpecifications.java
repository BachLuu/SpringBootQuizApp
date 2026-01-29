package com.example.springbootweb.repositories.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.springbootweb.entities.dtos.quizzes.QuizFilter;
import com.example.springbootweb.entities.models.Quiz;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Data JPA Specifications for Quiz entity. Provides reusable, composable query
 * predicates for dynamic filtering.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizSpecifications {

	// ==================== Basic Field Specifications ====================

	/**
	 * Filter by title keyword (partial match, case-insensitive)
	 */
	public static Specification<Quiz> titleContains(String keyword) {
		return (root, query, cb) -> keyword == null || keyword.isBlank() ? null
				: cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
	}

	/**
	 * Filter by active status
	 */
	public static Specification<Quiz> isActive(Boolean isActive) {
		return (root, query, cb) -> isActive == null ? null : cb.equal(root.get("isActive"), isActive);
	}

	/**
	 * Filter by minimum duration
	 */
	public static Specification<Quiz> minDuration(Integer minDuration) {
		return (root, query, cb) -> minDuration == null ? null
				: cb.greaterThanOrEqualTo(root.get("duration"), minDuration);
	}

	/**
	 * Filter by maximum duration
	 */
	public static Specification<Quiz> maxDuration(Integer maxDuration) {
		return (root, query, cb) -> maxDuration == null ? null
				: cb.lessThanOrEqualTo(root.get("duration"), maxDuration);
	}

	/**
	 * Filter by duration range
	 */
	public static Specification<Quiz> durationBetween(Integer min, Integer max) {
		return (root, query, cb) -> {
			if (min == null && max == null) {
				return null;
			}
			if (min != null && max != null) {
				return cb.between(root.get("duration"), min, max);
			}
			if (min != null) {
				return cb.greaterThanOrEqualTo(root.get("duration"), min);
			}
			return cb.lessThanOrEqualTo(root.get("duration"), max);
		};
	}

	// ==================== Combined Filter Specification ====================

	/**
	 * Build Specification from QuizFilter object. Service calls this method - NOT
	 * Controller.
	 * @param filter the filter criteria
	 * @return combined Specification
	 */
	public static Specification<Quiz> fromFilter(QuizFilter filter) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filter.getTitleKeyword() != null && !filter.getTitleKeyword().isBlank()) {
				predicates.add(
						cb.like(cb.lower(root.get("title")), "%" + filter.getTitleKeyword().toLowerCase() + "%"));
			}

			if (filter.getIsActive() != null) {
				predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
			}

			if (filter.getMinDuration() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("duration"), filter.getMinDuration()));
			}

			if (filter.getMaxDuration() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("duration"), filter.getMaxDuration()));
			}

			return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
