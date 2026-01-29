package com.example.springbootweb.repositories.specifications;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionFilter;
import com.example.springbootweb.entities.enums.QuizSessionStatus;
import com.example.springbootweb.entities.models.Quiz;
import com.example.springbootweb.entities.models.QuizSession;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Data JPA Specifications for QuizSession entity.
 * Provides reusable, composable query predicates for dynamic filtering.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizSessionSpecifications {

	// ==================== Basic Field Specifications ====================

	/**
	 * Filter by user ID
	 */
	public static Specification<QuizSession> hasUserId(UUID userId) {
		return (root, query, cb) -> userId == null ? null : cb.equal(root.get("userId"), userId);
	}

	/**
	 * Filter by quiz ID
	 */
	public static Specification<QuizSession> hasQuizId(UUID quizId) {
		return (root, query, cb) -> quizId == null ? null : cb.equal(root.get("quizId"), quizId);
	}

	/**
	 * Filter by single status
	 */
	public static Specification<QuizSession> hasStatus(QuizSessionStatus status) {
		return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
	}

	/**
	 * Filter by multiple statuses (IN clause)
	 */
	public static Specification<QuizSession> statusIn(Set<QuizSessionStatus> statuses) {
		return (root, query, cb) -> {
			if (statuses == null || statuses.isEmpty()) {
				return null;
			}
			return root.get("status").in(statuses);
		};
	}

	/**
	 * Filter by pass status
	 */
	public static Specification<QuizSession> isPassed(Boolean isPassed) {
		return (root, query, cb) -> isPassed == null ? null : cb.equal(root.get("isPassed"), isPassed);
	}

	// ==================== Date Range Specifications ====================

	/**
	 * Filter sessions started after a specific date
	 */
	public static Specification<QuizSession> startedAfter(LocalDateTime dateTime) {
		return (root, query, cb) -> dateTime == null ? null
				: cb.greaterThanOrEqualTo(root.get("startedAt"), dateTime);
	}

	/**
	 * Filter sessions started before a specific date
	 */
	public static Specification<QuizSession> startedBefore(LocalDateTime dateTime) {
		return (root, query, cb) -> dateTime == null ? null : cb.lessThanOrEqualTo(root.get("startedAt"), dateTime);
	}

	/**
	 * Filter sessions started between two dates
	 */
	public static Specification<QuizSession> startedBetween(LocalDateTime from, LocalDateTime to) {
		return (root, query, cb) -> {
			if (from == null && to == null) {
				return null;
			}
			if (from != null && to != null) {
				return cb.between(root.get("startedAt"), from, to);
			}
			if (from != null) {
				return cb.greaterThanOrEqualTo(root.get("startedAt"), from);
			}
			return cb.lessThanOrEqualTo(root.get("startedAt"), to);
		};
	}

	/**
	 * Filter sessions finished after a specific date
	 */
	public static Specification<QuizSession> finishedAfter(LocalDateTime dateTime) {
		return (root, query, cb) -> dateTime == null ? null
				: cb.greaterThanOrEqualTo(root.get("finishedAt"), dateTime);
	}

	/**
	 * Filter sessions finished before a specific date
	 */
	public static Specification<QuizSession> finishedBefore(LocalDateTime dateTime) {
		return (root, query, cb) -> dateTime == null ? null : cb.lessThanOrEqualTo(root.get("finishedAt"), dateTime);
	}

	/**
	 * Filter sessions finished between two dates
	 */
	public static Specification<QuizSession> finishedBetween(LocalDateTime from, LocalDateTime to) {
		return (root, query, cb) -> {
			if (from == null && to == null) {
				return null;
			}
			if (from != null && to != null) {
				return cb.between(root.get("finishedAt"), from, to);
			}
			if (from != null) {
				return cb.greaterThanOrEqualTo(root.get("finishedAt"), from);
			}
			return cb.lessThanOrEqualTo(root.get("finishedAt"), to);
		};
	}

	// ==================== Score Specifications ====================

	/**
	 * Filter by minimum score
	 */
	public static Specification<QuizSession> minScore(BigDecimal score) {
		return (root, query, cb) -> score == null ? null : cb.greaterThanOrEqualTo(root.get("score"), score);
	}

	/**
	 * Filter by maximum score
	 */
	public static Specification<QuizSession> maxScore(BigDecimal score) {
		return (root, query, cb) -> score == null ? null : cb.lessThanOrEqualTo(root.get("score"), score);
	}

	/**
	 * Filter by score range
	 */
	public static Specification<QuizSession> scoreBetween(BigDecimal min, BigDecimal max) {
		return (root, query, cb) -> {
			if (min == null && max == null) {
				return null;
			}
			if (min != null && max != null) {
				return cb.between(root.get("score"), min, max);
			}
			if (min != null) {
				return cb.greaterThanOrEqualTo(root.get("score"), min);
			}
			return cb.lessThanOrEqualTo(root.get("score"), max);
		};
	}

	// ==================== Join Specifications ====================

	/**
	 * Filter by quiz title (partial match, case-insensitive)
	 */
	public static Specification<QuizSession> quizTitleContains(String keyword) {
		return (root, query, cb) -> {
			if (keyword == null || keyword.isBlank()) {
				return null;
			}
			Join<QuizSession, Quiz> quizJoin = root.join("quiz", JoinType.LEFT);
			return cb.like(cb.lower(quizJoin.get("title")), "%" + keyword.toLowerCase() + "%");
		};
	}

	// ==================== Composite Specifications ====================

	/**
	 * Build specification from filter object
	 */
	public static Specification<QuizSession> fromFilter(QuizSessionFilter filter) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (filter.getUserId() != null) {
				predicates.add(cb.equal(root.get("userId"), filter.getUserId()));
			}

			if (filter.getQuizId() != null) {
				predicates.add(cb.equal(root.get("quizId"), filter.getQuizId()));
			}

			if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
				predicates.add(root.get("status").in(filter.getStatuses()));
			}

			if (filter.getStartedAfter() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("startedAt"), filter.getStartedAfter()));
			}

			if (filter.getStartedBefore() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("startedAt"), filter.getStartedBefore()));
			}

			if (filter.getFinishedAfter() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("finishedAt"), filter.getFinishedAfter()));
			}

			if (filter.getFinishedBefore() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("finishedAt"), filter.getFinishedBefore()));
			}

			if (filter.getMinScore() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("score"), filter.getMinScore()));
			}

			if (filter.getMaxScore() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("score"), filter.getMaxScore()));
			}

			if (filter.getIsPassed() != null) {
				predicates.add(cb.equal(root.get("isPassed"), filter.getIsPassed()));
			}

			if (filter.getQuizTitleKeyword() != null && !filter.getQuizTitleKeyword().isBlank()) {
				Join<QuizSession, Quiz> quizJoin = root.join("quiz", JoinType.LEFT);
				predicates.add(
						cb.like(cb.lower(quizJoin.get("title")), "%" + filter.getQuizTitleKeyword().toLowerCase() + "%"));
			}

			return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	/**
	 * Completed sessions only (SUBMITTED, GRADED, or TIMED_OUT)
	 */
	public static Specification<QuizSession> completedOnly() {
		return statusIn(Set.of(QuizSessionStatus.SUBMITTED, QuizSessionStatus.GRADED, QuizSessionStatus.TIMED_OUT));
	}

	/**
	 * Active sessions only (NOT_STARTED, IN_PROGRESS, or PAUSED)
	 */
	public static Specification<QuizSession> activeOnly() {
		return statusIn(
				Set.of(QuizSessionStatus.NOT_STARTED, QuizSessionStatus.IN_PROGRESS, QuizSessionStatus.PAUSED));
	}

	/**
	 * Order by created date descending (newest first)
	 */
	public static Specification<QuizSession> orderByCreatedAtDesc() {
		return (root, query, cb) -> {
			query.orderBy(cb.desc(root.get("createdAt")));
			return null;
		};
	}

	/**
	 * Order by score descending (highest first)
	 */
	public static Specification<QuizSession> orderByScoreDesc() {
		return (root, query, cb) -> {
			query.orderBy(cb.desc(root.get("score")));
			return null;
		};
	}

}
