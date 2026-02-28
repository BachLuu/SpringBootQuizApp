package com.example.springbootweb.repositories.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.example.springbootweb.entities.dtos.questions.QuestionFilter;
import com.example.springbootweb.entities.enums.QuestionType;
import com.example.springbootweb.entities.models.Question;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Data JPA Specifications for Question entity. Provides reusable, composable query
 * predicates for dynamic filtering.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionSpecifications {

	// ==================== Basic Field Specifications ====================

	/**
	 * Filter by content keyword (partial match, case-insensitive)
	 */
	public static Specification<Question> contentContains(String keyword) {
		return (root, query, cb) -> keyword == null || keyword.isBlank() ? null
				: cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%");
	}

	/**
	 * Filter by single question type
	 */
	public static Specification<Question> hasQuestionType(QuestionType type) {
		return (root, query, cb) -> type == null ? null : cb.equal(root.get("questionType"), type);
	}

	/**
	 * Filter by multiple question types (IN clause)
	 */
	public static Specification<Question> questionTypeIn(Set<QuestionType> types) {
		return (root, query, cb) -> {
			if (types == null || types.isEmpty()) {
				return null;
			}
			return root.get("questionType").in(types);
		};
	}

	/**
	 * Filter by active status
	 */
	public static Specification<Question> isActive(Boolean isActive) {
		return (root, query, cb) -> isActive == null ? null : cb.equal(root.get("isActive"), isActive);
	}

	// ==================== Combined Filter Specification ====================

	/**
	 * Build Specification from QuestionFilter object. Service calls this method - NOT
	 * Controller.
	 * @param filter the filter criteria
	 * @return combined Specification
	 */
	public static Specification<Question> fromFilter(QuestionFilter filter) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filter.getContentKeyword() != null && !filter.getContentKeyword().isBlank()) {
				predicates.add(
						cb.like(cb.lower(root.get("content")), "%" + filter.getContentKeyword().toLowerCase() + "%"));
			}

			if (filter.getQuestionTypes() != null && !filter.getQuestionTypes().isEmpty()) {
				predicates.add(root.get("questionType").in(filter.getQuestionTypes()));
			}

			if (filter.getIsActive() != null) {
				predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
			}

			return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
