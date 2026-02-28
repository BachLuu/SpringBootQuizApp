package com.example.springbootweb.entities.enums;

/**
 * Represents the status of a quiz session
 */
public enum QuizSessionStatus {

	/**
	 * Session created but not started yet
	 */
	NOT_STARTED,

	/**
	 * User is actively taking the quiz
	 */
	IN_PROGRESS,

	/**
	 * Session is paused (if allowed)
	 */
	PAUSED,

	/**
	 * User has submitted the quiz
	 */
	SUBMITTED,

	/**
	 * Quiz has been auto-submitted due to timeout
	 */
	TIMED_OUT,

	/**
	 * Quiz has been graded (for manual review questions)
	 */
	GRADED,

	/**
	 * Session was abandoned/cancelled
	 */
	ABANDONED

}
