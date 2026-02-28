package com.example.springbootweb.entities.enums;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing difficulty levels for questions based on correct answer rate. Uses
 * threshold-based classification with built-in logic.
 */
@Getter
@AllArgsConstructor
public enum DifficultyLevel {

	VERY_EASY(80, "Very Easy - Most users answer correctly"), EASY(60, "Easy - Majority of users answer correctly"),
	MEDIUM(40, "Medium - Moderate difficulty"), HARD(20, "Hard - Challenging for most users"),
	VERY_HARD(0, "Very Hard - Most users struggle"), UNKNOWN(-1, "Unknown - Not enough data");

	private final int minCorrectRate;

	private final String description;

	/**
	 * Determines difficulty level from a correct answer rate percentage.
	 * @param correctRate percentage of correct answers (0-100)
	 * @return corresponding DifficultyLevel
	 */
	public static DifficultyLevel fromCorrectRate(BigDecimal correctRate) {
		if (correctRate == null) {
			return UNKNOWN;
		}

		int rate = correctRate.intValue();

		if (rate >= VERY_EASY.minCorrectRate) {
			return VERY_EASY;
		}
		else if (rate >= EASY.minCorrectRate) {
			return EASY;
		}
		else if (rate >= MEDIUM.minCorrectRate) {
			return MEDIUM;
		}
		else if (rate >= HARD.minCorrectRate) {
			return HARD;
		}
		else {
			return VERY_HARD;
		}
	}

	/**
	 * Checks if this difficulty level is harder than another.
	 */
	public boolean isHarderThan(DifficultyLevel other) {
		if (this == UNKNOWN || other == UNKNOWN) {
			return false;
		}
		return this.minCorrectRate < other.minCorrectRate;
	}

	/**
	 * Checks if this difficulty level is easier than another.
	 */
	public boolean isEasierThan(DifficultyLevel other) {
		if (this == UNKNOWN || other == UNKNOWN) {
			return false;
		}
		return this.minCorrectRate > other.minCorrectRate;
	}

}
