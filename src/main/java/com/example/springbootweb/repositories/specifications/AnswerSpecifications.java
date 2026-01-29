package com.example.springbootweb.repositories.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.example.springbootweb.entities.dtos.answers.AnswerFilter;
import com.example.springbootweb.entities.models.Answer;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Data JPA Specifications for Answer entity. Provides reusable, composable query
 * predicates for dynamic filtering.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerSpecifications {

	// ==================== Basic Field Specifications ====================

	/**
	 * Filter by content keyword (partial match, case-insensitive)
	 */
	public static Specification<Answer> contentContains(String keyword) {
		return (root, query, cb) -> keyword == null || keyword.isBlank() ? null
				: cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%");
	}

	/**
	 * Filter by question ID
	 */
	public static Specification<Answer> hasQuestionId(UUID questionId) {
		return (root, query, cb) -> questionId == null ? null : cb.equal(root.get("questionId"), questionId);
	}

	/**
	 * Filter by correct status
	 */
	public static Specification<Answer> isCorrect(Boolean isCorrect) {
		return (root, query, cb) -> isCorrect == null ? null : cb.equal(root.get("isCorrect"), isCorrect);
	}

	/**
	 * Filter by active status
	 */
	public static Specification<Answer> isActive(Boolean isActive) {
		return (root, query, cb) -> isActive == null ? null : cb.equal(root.get("isActive"), isActive);
	}

	// ==================== Combined Filter Specification ====================

	/**
	 * Build Specification from AnswerFilter object. Service calls this method - NOT
	 * Controller.
	 * @param filter the filter criteria
	 * @return combined Specification
	 */
	public static Specification<Answer> fromFilter(AnswerFilter filter) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filter.getContentKeyword() != null && !filter.getContentKeyword().isBlank()) {
				predicates.add(
						cb.like(cb.lower(root.get("content")), "%" + filter.getContentKeyword().toLowerCase() + "%"));
			}

			if (filter.getQuestionId() != null) {
				predicates.add(cb.equal(root.get("questionId"), filter.getQuestionId()));
			}

			if (filter.getIsCorrect() != null) {
				predicates.add(cb.equal(root.get("isCorrect"), filter.getIsCorrect()));
			}

			if (filter.getIsActive() != null) {
				predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
			}

			return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
