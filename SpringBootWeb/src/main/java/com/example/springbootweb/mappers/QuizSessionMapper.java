package com.example.springbootweb.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.example.springbootweb.entities.dtos.quizsessions.LeaderboardResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionDetailResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionSummaryResponse;
import com.example.springbootweb.entities.models.Quiz;
import com.example.springbootweb.entities.models.QuizSession;
import com.example.springbootweb.entities.models.User;

/**
 * MapStruct mapper for QuizSession entity. Provides mapping methods for detail, summary
 * and leaderboard responses.
 */
@Mapper(config = CommonMapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuizSessionMapper {

	// ==================== Detail Response ====================

	/**
	 * Map QuizSession to QuizSessionDetailResponse. Requires Quiz and User context for
	 * derived fields.
	 * @param session the quiz session entity
	 * @param quiz the associated quiz
	 * @param user the user taking the quiz
	 * @param remainingTimeSeconds calculated remaining time
	 * @return QuizSessionDetailResponse
	 */
	@Mapping(target = "quizId", source = "session.quizId")
	@Mapping(target = "quizTitle", source = "quiz.title")
	@Mapping(target = "userId", source = "session.userId")
	@Mapping(target = "userName", source = "user.displayName")
	@Mapping(target = "id", source = "session.id")
	@Mapping(target = "status", source = "session.status")
	@Mapping(target = "createdAt", source = "session.createdAt")
	@Mapping(target = "startedAt", source = "session.startedAt")
	@Mapping(target = "finishedAt", source = "session.finishedAt")
	@Mapping(target = "expiresAt", source = "session.expiresAt")
	@Mapping(target = "timeSpentSeconds", source = "session.timeSpentSeconds")
	@Mapping(target = "totalQuestions", source = "session.totalQuestions")
	@Mapping(target = "answeredQuestions", source = "session.answeredQuestions")
	@Mapping(target = "correctAnswers", source = "session.correctAnswers")
	@Mapping(target = "score", source = "session.score")
	@Mapping(target = "pointsEarned", source = "session.pointsEarned")
	@Mapping(target = "maxPoints", source = "session.maxPoints")
	@Mapping(target = "isPassed", source = "session.isPassed")
	@Mapping(target = "currentQuestionIndex", source = "session.currentQuestionIndex")
	@Mapping(target = "remainingTimeSeconds", source = "remainingTimeSeconds")
	QuizSessionDetailResponse toDetailResponse(QuizSession session, Quiz quiz, User user, Integer remainingTimeSeconds);

	// ==================== Summary Response ====================

	/**
	 * Map QuizSession to QuizSessionSummaryResponse. Requires Quiz context for derived
	 * fields.
	 * @param session the quiz session entity
	 * @param quiz the associated quiz (can be null)
	 * @return QuizSessionSummaryResponse
	 */
	@Mapping(target = "id", source = "session.id")
	@Mapping(target = "quizId", source = "session.quizId")
	@Mapping(target = "quizTitle", source = "quiz.title")
	@Mapping(target = "quizThumbnail", source = "quiz.thumbnailUrl")
	@Mapping(target = "status", source = "session.status")
	@Mapping(target = "startedAt", source = "session.startedAt")
	@Mapping(target = "finishedAt", source = "session.finishedAt")
	@Mapping(target = "timeSpentSeconds", source = "session.timeSpentSeconds")
	@Mapping(target = "totalQuestions", source = "session.totalQuestions")
	@Mapping(target = "correctAnswers", source = "session.correctAnswers")
	@Mapping(target = "score", source = "session.score")
	@Mapping(target = "isPassed", source = "session.isPassed")
	QuizSessionSummaryResponse toSummaryResponse(QuizSession session, Quiz quiz);

	// ==================== Leaderboard ====================

	/**
	 * Map QuizSession to LeaderboardEntry.
	 * @param session the quiz session
	 * @param user the user who completed the session
	 * @param rank the calculated rank
	 * @return LeaderboardEntry
	 */
	@Mapping(target = "userId", source = "session.userId")
	@Mapping(target = "userName", expression = "java(user != null ? user.getDisplayName() : \"Unknown\")")
	@Mapping(target = "userAvatar", source = "user.avatar")
	@Mapping(target = "rank", source = "rank")
	@Mapping(target = "score", source = "session.score")
	@Mapping(target = "pointsEarned", source = "session.pointsEarned")
	@Mapping(target = "correctAnswers", source = "session.correctAnswers")
	@Mapping(target = "totalQuestions", source = "session.totalQuestions")
	@Mapping(target = "timeSpentSeconds", source = "session.timeSpentSeconds")
	@Mapping(target = "completedAt", source = "session.finishedAt")
	LeaderboardResponse.LeaderboardEntry toLeaderboardEntry(QuizSession session, User user, Integer rank);

	/**
	 * Build complete LeaderboardResponse from quiz and sessions.
	 * @param quiz the quiz
	 * @param entries the mapped leaderboard entries
	 * @return LeaderboardResponse
	 */
	@Mapping(target = "quizId", source = "quiz.id")
	@Mapping(target = "quizTitle", source = "quiz.title")
	@Mapping(target = "totalParticipants", expression = "java(entries != null ? entries.size() : 0)")
	@Mapping(target = "entries", source = "entries")
	LeaderboardResponse toLeaderboardResponse(Quiz quiz, List<LeaderboardResponse.LeaderboardEntry> entries);

}
