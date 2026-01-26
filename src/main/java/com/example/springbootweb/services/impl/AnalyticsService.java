package com.example.springbootweb.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbootweb.entities.dtos.analytics.AdminDashboardResponse;
import com.example.springbootweb.entities.dtos.analytics.AdminDashboardResponse.*;
import com.example.springbootweb.entities.enums.DifficultyLevel;
import com.example.springbootweb.entities.dtos.analytics.QuestionDifficultyResponse;
import com.example.springbootweb.entities.dtos.analytics.QuestionDifficultyResponse.*;
import com.example.springbootweb.entities.dtos.analytics.QuizStatisticsResponse;
import com.example.springbootweb.entities.dtos.analytics.QuizStatisticsResponse.*;
import com.example.springbootweb.entities.dtos.analytics.UserPerformanceResponse;
import com.example.springbootweb.entities.dtos.analytics.UserPerformanceResponse.*;
import com.example.springbootweb.entities.models.Question;
import com.example.springbootweb.entities.models.Quiz;
import com.example.springbootweb.entities.models.QuizSession;
import com.example.springbootweb.entities.models.User;
import com.example.springbootweb.entities.projections.analytics.*;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.repositories.AnalyticsRepository;
import com.example.springbootweb.repositories.QuestionRepository;
import com.example.springbootweb.repositories.QuizRepository;
import com.example.springbootweb.repositories.UserRepository;
import com.example.springbootweb.services.interfaces.IAnalyticsService;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for Analytics operations. Provides complex statistics and
 * reporting capabilities. Uses Interface-Based Projections for type-safe query results.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService implements IAnalyticsService {

	private static final Logger LOG = LoggerFactory.getLogger(AnalyticsService.class);

	private final AnalyticsRepository analyticsRepository;

	private final QuizRepository quizRepository;

	private final UserRepository userRepository;

	private final QuestionRepository questionRepository;

	// ==================== Quiz Statistics ====================

	@Override
	public QuizStatisticsResponse getQuizStatistics(UUID quizId) {
		LOG.info("Getting statistics for quiz: {}", quizId);

		Quiz quiz = quizRepository.findById(quizId)
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

		// Get basic statistics using type-safe projection
		QuizBasicStatsProjection basicStats = analyticsRepository.getQuizBasicStatistics(quizId);
		Long totalAttempts = nullSafe(basicStats.getTotalAttempts());
		Long completedAttempts = nullSafe(basicStats.getCompletedAttempts());
		Long passedAttempts = nullSafe(basicStats.getPassedAttempts());
		Long failedAttempts = nullSafe(basicStats.getFailedAttempts());

		// Calculate rates
		BigDecimal passRate = calculateRate(passedAttempts, completedAttempts);
		BigDecimal failRate = calculateRate(failedAttempts, completedAttempts);
		BigDecimal completionRate = calculateRate(completedAttempts, totalAttempts);

		// Get score statistics using type-safe projection
		ScoreStatsProjection scoreStats = analyticsRepository.getQuizScoreStatistics(quizId);
		BigDecimal avgScore = nullSafe(scoreStats.getAvgScore());
		BigDecimal maxScore = nullSafe(scoreStats.getMaxScore());
		BigDecimal minScore = nullSafe(scoreStats.getMinScore());

		// Get time statistics using type-safe projection
		TimeStatsProjection timeStats = analyticsRepository.getQuizTimeStatistics(quizId);
		Integer avgTime = toInteger(timeStats.getAvgTime());
		Integer minTime = nullSafe(timeStats.getMinTime());
		Integer maxTime = nullSafe(timeStats.getMaxTime());

		// Get question performance
		List<QuestionPerformanceDto> questionPerformance = getQuestionPerformanceList(quizId);

		// Get score distribution
		List<ScoreDistributionDto> scoreDistribution = getScoreDistributionList(quizId, completedAttempts);

		return new QuizStatisticsResponse(quizId, quiz.getTitle(), totalAttempts, completedAttempts, passedAttempts,
				failedAttempts, passRate, failRate, completionRate, avgScore, maxScore, minScore, avgScore, // median
																											// approximation
				avgTime, minTime, maxTime, questionPerformance, scoreDistribution);
	}

	private List<QuestionPerformanceDto> getQuestionPerformanceList(UUID quizId) {
		List<QuestionPerformanceProjection> results = analyticsRepository.getQuestionPerformance(quizId);

		return results.stream().map(proj -> {
			Long total = nullSafe(proj.getTotalAnswers());
			Long correct = nullSafe(proj.getCorrectAnswers());
			BigDecimal correctRate = calculateRate(correct, total);
			DifficultyLevel difficultyLevel = DifficultyLevel.fromCorrectRate(correctRate);

			return new QuestionPerformanceDto(proj.getQuestionId(), truncateString(proj.getContent(), 100),
					proj.getType() != null ? proj.getType().name() : "UNKNOWN", total, correct,
					nullSafe(proj.getIncorrectAnswers()), correctRate, toInteger(proj.getAvgTime()), difficultyLevel);
		}).collect(Collectors.toList());
	}

	private List<ScoreDistributionDto> getScoreDistributionList(UUID quizId, Long totalCompleted) {
		List<ScoreDistributionProjection> results = analyticsRepository.getScoreDistribution(quizId);

		return results.stream().map(proj -> {
			Long count = nullSafe(proj.getCount());
			BigDecimal percentage = calculateRate(count, totalCompleted);
			return new ScoreDistributionDto(proj.getScoreRange(), count, percentage);
		}).collect(Collectors.toList());
	}

	// ==================== User Performance ====================

	@Override
	public UserPerformanceResponse getUserPerformance(UUID userId) {
		LOG.info("Getting performance for user: {}", userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		// Get overview stats
		OverviewStats overview = buildUserOverviewStats(userId);

		// Get performance by question type
		List<QuestionTypePerformanceDto> performanceByType = buildQuestionTypePerformance(userId);

		// Get recent attempts
		List<RecentQuizAttemptDto> recentAttempts = buildRecentAttempts(userId);

		// Get progress over time
		List<ProgressDataDto> progressOverTime = buildProgressOverTime(userId);

		// Build strengths and weaknesses
		StrengthsWeaknessesDto strengthsWeaknesses = buildStrengthsWeaknesses(performanceByType);

		return new UserPerformanceResponse(userId, user.getFirstName() + " " + user.getLastName(), user.getEmail(),
				overview, performanceByType, recentAttempts, progressOverTime, strengthsWeaknesses);
	}

	private OverviewStats buildUserOverviewStats(UUID userId) {
		UserOverviewStatsProjection overviewStats = analyticsRepository.getUserOverviewStats(userId);
		UserAnswerStatsProjection answerStats = analyticsRepository.getUserAnswerStats(userId);

		Long totalQuizzes = nullSafe(overviewStats.getTotalQuizzes());
		Long passedQuizzes = nullSafe(overviewStats.getPassedQuizzes());
		Long failedQuizzes = nullSafe(overviewStats.getFailedQuizzes());
		BigDecimal avgScore = nullSafe(overviewStats.getAvgScore());
		BigDecimal maxScore = nullSafe(overviewStats.getMaxScore());
		BigDecimal minScore = nullSafe(overviewStats.getMinScore());
		Long totalSeconds = nullSafe(overviewStats.getTotalTimeSeconds());
		Integer totalTimeMinutes = totalSeconds != null ? (int) (totalSeconds / 60) : 0;

		Long totalAnswered = nullSafe(answerStats.getTotalAnswered());
		Long totalCorrect = nullSafe(answerStats.getTotalCorrect());

		BigDecimal passRate = calculateRate(passedQuizzes, totalQuizzes);
		BigDecimal accuracy = calculateRate(totalCorrect, totalAnswered);

		return new OverviewStats(totalQuizzes, passedQuizzes, failedQuizzes, passRate, avgScore, maxScore, minScore,
				totalTimeMinutes, totalAnswered, totalCorrect, accuracy);
	}

	private List<QuestionTypePerformanceDto> buildQuestionTypePerformance(UUID userId) {
		List<QuestionTypePerformanceProjection> results = analyticsRepository.getUserPerformanceByQuestionType(userId);

		return results.stream().map(proj -> {
			Long total = nullSafe(proj.getTotalAnswered());
			Long correct = nullSafe(proj.getCorrectAnswers());
			BigDecimal accuracy = calculateRate(correct, total);

			return new QuestionTypePerformanceDto(proj.getType() != null ? proj.getType().name() : "UNKNOWN", total,
					correct, accuracy, toInteger(proj.getAvgTime()));
		}).collect(Collectors.toList());
	}

	private List<RecentQuizAttemptDto> buildRecentAttempts(UUID userId) {
		Pageable pageable = PageRequest.of(0, 10);
		List<QuizSession> sessions = analyticsRepository.getUserRecentAttempts(userId, pageable);

		return sessions.stream().map(session -> {
			String quizTitle = quizRepository.findById(session.getQuizId()).map(Quiz::getTitle).orElse("Unknown Quiz");

			return new RecentQuizAttemptDto(session.getId(), session.getQuizId(), quizTitle, session.getScore(),
					session.getIsPassed(), session.getTimeSpentSeconds(), session.getFinishedAt());
		}).collect(Collectors.toList());
	}

	private List<ProgressDataDto> buildProgressOverTime(UUID userId) {
		LocalDateTime startDate = LocalDateTime.now().minus(6, ChronoUnit.MONTHS);
		List<UserProgressProjection> results = analyticsRepository.getUserProgressOverTime(userId, startDate);

		return results.stream()
			.map(proj -> new ProgressDataDto(proj.getPeriod(), nullSafe(proj.getQuizzesTaken()),
					nullSafe(proj.getAvgScore()), toBigDecimal(proj.getPassRate())))
			.collect(Collectors.toList());
	}

	private StrengthsWeaknessesDto buildStrengthsWeaknesses(List<QuestionTypePerformanceDto> performanceByType) {
		List<String> strengths = new ArrayList<>();
		List<String> weaknesses = new ArrayList<>();
		List<String> recommendations = new ArrayList<>();

		for (QuestionTypePerformanceDto perf : performanceByType) {
			if (perf.accuracy() != null) {
				if (perf.accuracy().compareTo(BigDecimal.valueOf(70)) >= 0) {
					strengths.add(perf.questionType() + " questions (Accuracy: " + perf.accuracy() + "%)");
				}
				else if (perf.accuracy().compareTo(BigDecimal.valueOf(50)) < 0) {
					weaknesses.add(perf.questionType() + " questions (Accuracy: " + perf.accuracy() + "%)");
					recommendations.add("Practice more " + perf.questionType() + " questions to improve your skills");
				}
			}
		}

		if (strengths.isEmpty()) {
			strengths.add("Continue practicing to develop your strengths");
		}
		if (weaknesses.isEmpty()) {
			weaknesses.add("No significant weaknesses identified");
		}
		if (recommendations.isEmpty()) {
			recommendations.add("Maintain consistency and challenge yourself with harder quizzes");
		}

		return new StrengthsWeaknessesDto(strengths, weaknesses, recommendations);
	}

	// ==================== Admin Dashboard ====================

	@Override
	public AdminDashboardResponse getAdminDashboard() {
		LOG.info("Building admin dashboard");

		SystemOverviewDto systemOverview = buildSystemOverview();
		ActivityStatsDto activityStats = buildActivityStats();
		List<TopPerformerDto> topPerformers = buildTopPerformers();
		List<PopularQuizDto> popularQuizzes = buildPopularQuizzes();
		List<RecentActivityDto> recentActivities = buildRecentActivities();
		List<QuizCompletionDto> quizCompletionRates = buildQuizCompletionRates();

		return new AdminDashboardResponse(systemOverview, activityStats, topPerformers, popularQuizzes,
				recentActivities, quizCompletionRates);
	}

	private SystemOverviewDto buildSystemOverview() {
		Long totalUsers = userRepository.count();
		Long activeUsers = analyticsRepository.countActiveUsers();
		Long totalQuizzes = quizRepository.count();
		Long activeQuizzes = (long) quizRepository.findByIsActiveTrue().size();
		Long totalQuestions = questionRepository.count();
		Long totalAttempts = analyticsRepository.count();
		Long completedAttempts = totalAttempts; // Fallback

		BigDecimal overallPassRate = analyticsRepository.getOverallPassRate();
		if (overallPassRate == null) {
			overallPassRate = BigDecimal.ZERO;
		}

		return new SystemOverviewDto(totalUsers, activeUsers, totalQuizzes, activeQuizzes, totalQuestions,
				totalAttempts, completedAttempts, overallPassRate.setScale(2, RoundingMode.HALF_UP));
	}

	private ActivityStatsDto buildActivityStats() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime todayStart = now.truncatedTo(ChronoUnit.DAYS);
		LocalDateTime weekStart = now.minus(7, ChronoUnit.DAYS);
		LocalDateTime monthStart = now.minus(30, ChronoUnit.DAYS);

		ActivityStatsProjection activityStats = analyticsRepository.getActivityStats(todayStart, weekStart, monthStart);

		Long newUsersThisWeek = analyticsRepository.countNewUsersSince(weekStart);
		Long newUsersThisMonth = analyticsRepository.countNewUsersSince(monthStart);

		BigDecimal avgScoreThisWeek = analyticsRepository.getAverageScoreSince(weekStart);
		BigDecimal avgScoreThisMonth = analyticsRepository.getAverageScoreSince(monthStart);

		return new ActivityStatsDto(nullSafe(activityStats.getAttemptsToday()),
				nullSafe(activityStats.getAttemptsThisWeek()), nullSafe(activityStats.getAttemptsThisMonth()),
				nullSafe(newUsersThisWeek), nullSafe(newUsersThisMonth),
				nullSafe(avgScoreThisWeek).setScale(2, RoundingMode.HALF_UP),
				nullSafe(avgScoreThisMonth).setScale(2, RoundingMode.HALF_UP));
	}

	private List<TopPerformerDto> buildTopPerformers() {
		Pageable pageable = PageRequest.of(0, 10);
		List<TopPerformerProjection> results = analyticsRepository.getTopPerformers(pageable);

		List<TopPerformerDto> performers = new ArrayList<>();
		int rank = 1;

		for (TopPerformerProjection proj : results) {
			UUID userId = proj.getUserId();
			Long quizzesTaken = nullSafe(proj.getQuizzesTaken());
			Long quizzesPassed = nullSafe(proj.getQuizzesPassed());
			BigDecimal avgScore = nullSafe(proj.getAvgScore());
			BigDecimal passRate = calculateRate(quizzesPassed, quizzesTaken);

			User user = userRepository.findById(userId).orElse(null);
			String userName = user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown";
			String userEmail = user != null ? user.getEmail() : "unknown@email.com";

			performers.add(new TopPerformerDto(rank++, userId, userName, userEmail, quizzesTaken, quizzesPassed,
					avgScore, passRate));
		}

		return performers;
	}

	private List<PopularQuizDto> buildPopularQuizzes() {
		Pageable pageable = PageRequest.of(0, 10);
		List<PopularQuizProjection> results = analyticsRepository.getPopularQuizzes(pageable);

		return results.stream().map(proj -> {
			String quizTitle = quizRepository.findById(proj.getQuizId()).map(Quiz::getTitle).orElse("Unknown Quiz");

			return new PopularQuizDto(proj.getQuizId(), quizTitle, nullSafe(proj.getTotalAttempts()),
					nullSafe(proj.getCompletedAttempts()),
					toBigDecimal(proj.getPassRate()).setScale(2, RoundingMode.HALF_UP),
					nullSafe(proj.getAvgScore()).setScale(2, RoundingMode.HALF_UP));
		}).collect(Collectors.toList());
	}

	private List<RecentActivityDto> buildRecentActivities() {
		Pageable pageable = PageRequest.of(0, 15);
		List<QuizSession> sessions = analyticsRepository.getRecentCompletedActivities(pageable);

		return sessions.stream().map(session -> {
			User user = userRepository.findById(session.getUserId()).orElse(null);
			String userName = user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown";

			String quizTitle = quizRepository.findById(session.getQuizId()).map(Quiz::getTitle).orElse("Unknown Quiz");

			return new RecentActivityDto(session.getId(), session.getUserId(), userName, session.getQuizId(), quizTitle,
					session.getStatus().name(), session.getScore(), session.getIsPassed(), session.getFinishedAt());
		}).collect(Collectors.toList());
	}

	private List<QuizCompletionDto> buildQuizCompletionRates() {
		List<QuizCompletionProjection> results = analyticsRepository.getQuizCompletionRates();

		return results.stream().map(proj -> {
			Long totalAttempts = nullSafe(proj.getTotalAttempts());
			Long completedAttempts = nullSafe(proj.getCompletedAttempts());
			BigDecimal completionRate = calculateRate(completedAttempts, totalAttempts);

			String quizTitle = quizRepository.findById(proj.getQuizId()).map(Quiz::getTitle).orElse("Unknown Quiz");

			return new QuizCompletionDto(proj.getQuizId(), quizTitle, totalAttempts, completedAttempts,
					nullSafe(proj.getAbandonedAttempts()), completionRate, BigDecimal.ZERO // pass
																							// rate
																							// -
																							// would
																							// need
																							// separate
																							// query
			);
		}).collect(Collectors.toList());
	}

	// ==================== Question Difficulty Analysis ====================

	@Override
	public QuestionDifficultyResponse getQuestionDifficultyAnalysis(UUID questionId) {
		LOG.info("Getting difficulty analysis for question: {}", questionId);

		Question question = questionRepository.findById(questionId)
			.orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

		DifficultyMetricsDto metrics = buildDifficultyMetrics(questionId);
		List<AnswerDistributionDto> answerDistribution = buildAnswerDistribution(questionId, metrics.totalAttempts());
		TimeAnalysisDto timeAnalysis = buildTimeAnalysis(questionId);
		List<String> recommendations = buildDifficultyRecommendations(metrics, answerDistribution);

		return new QuestionDifficultyResponse(questionId, question.getContent(), question.getQuestionType().name(),
				metrics, answerDistribution, timeAnalysis, recommendations);
	}

	private DifficultyMetricsDto buildDifficultyMetrics(UUID questionId) {
		QuestionDifficultyProjection proj = analyticsRepository.getQuestionDifficultyMetrics(questionId);

		Long totalAttempts = nullSafe(proj.getTotalAttempts());
		Long correctAttempts = nullSafe(proj.getCorrectAttempts());
		Long incorrectAttempts = nullSafe(proj.getIncorrectAttempts());
		Long skippedAttempts = nullSafe(proj.getSkippedAttempts());

		BigDecimal correctRate = calculateRate(correctAttempts, totalAttempts);
		BigDecimal incorrectRate = calculateRate(incorrectAttempts, totalAttempts);
		BigDecimal skippedRate = calculateRate(skippedAttempts, totalAttempts);

		DifficultyLevel difficultyLevel = DifficultyLevel.fromCorrectRate(correctRate);
		BigDecimal difficultyScore = BigDecimal.valueOf(100).subtract(correctRate);

		return new DifficultyMetricsDto(totalAttempts, correctAttempts, incorrectAttempts, skippedAttempts, correctRate,
				incorrectRate, skippedRate, difficultyLevel, difficultyScore);
	}

	private List<AnswerDistributionDto> buildAnswerDistribution(UUID questionId, Long totalAttempts) {
		List<AnswerDistributionProjection> results = analyticsRepository.getAnswerDistribution(questionId);

		return results.stream().map(proj -> {
			Long selectedCount = nullSafe(proj.getSelectedCount());
			BigDecimal selectionRate = calculateRate(selectedCount, totalAttempts);

			return new AnswerDistributionDto(proj.getAnswerId(), truncateString(proj.getContent(), 100),
					proj.getIsCorrect(), selectedCount, selectionRate);
		}).collect(Collectors.toList());
	}

	private TimeAnalysisDto buildTimeAnalysis(UUID questionId) {
		QuestionTimeAnalysisProjection proj = analyticsRepository.getQuestionTimeAnalysis(questionId);

		Integer avgTime = toInteger(proj.getAvgTime());
		Integer minTime = nullSafe(proj.getMinTime());
		Integer maxTime = nullSafe(proj.getMaxTime());
		Integer avgTimeCorrect = toInteger(proj.getAvgTimeCorrect());
		Integer avgTimeIncorrect = toInteger(proj.getAvgTimeIncorrect());

		return new TimeAnalysisDto(avgTime, avgTime, // median approximation
				minTime, maxTime, avgTimeCorrect, avgTimeIncorrect);
	}

	private List<String> buildDifficultyRecommendations(DifficultyMetricsDto metrics,
			List<AnswerDistributionDto> answerDistribution) {
		List<String> recommendations = new ArrayList<>();

		switch (metrics.difficultyLevel()) {
			case VERY_EASY:
				recommendations.add("Consider making this question more challenging");
				recommendations.add("Add more similar correct-looking distractors");
				break;
			case EASY:
				recommendations.add("This question has good balance but could be slightly harder");
				break;
			case MEDIUM:
				recommendations.add("This question has optimal difficulty level");
				break;
			case HARD:
				recommendations.add("Review if the question wording is clear");
				recommendations.add("Check if correct answer is unambiguous");
				break;
			case VERY_HARD:
				recommendations.add("Consider revising this question - it may be too difficult");
				recommendations.add("Review if adequate learning materials cover this topic");
				recommendations.add("Check for potential issues with question clarity");
				break;
			default:
				recommendations.add("Unable to determine difficulty - not enough data");
				break;
		}

		if (metrics.skippedRate().compareTo(BigDecimal.valueOf(20)) > 0) {
			recommendations.add("High skip rate detected - review question difficulty and time allocation");
		}

		if (!answerDistribution.isEmpty()) {
			List<AnswerDistributionDto> distractors = answerDistribution.stream()
				.filter(a -> a.isCorrect() != null && !a.isCorrect())
				.filter(a -> a.selectionRate().compareTo(BigDecimal.valueOf(5)) < 0)
				.collect(Collectors.toList());

			if (!distractors.isEmpty()) {
				recommendations.add("Some distractors are too obvious - consider making them more plausible");
			}
		}

		return recommendations;
	}

	// ==================== Null-Safe Utility Methods ====================

	private Long nullSafe(Long value) {
		return value != null ? value : 0L;
	}

	private Integer nullSafe(Integer value) {
		return value != null ? value : 0;
	}

	private BigDecimal nullSafe(BigDecimal value) {
		return value != null ? value.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}

	private Integer toInteger(Double value) {
		return value != null ? value.intValue() : 0;
	}

	private BigDecimal toBigDecimal(Double value) {
		return value != null ? BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}

	private BigDecimal calculateRate(Long numerator, Long denominator) {
		if (denominator == null || denominator == 0 || numerator == null) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(numerator)
			.multiply(BigDecimal.valueOf(100))
			.divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
	}

	private String truncateString(String str, int maxLength) {
		if (str == null) {
			return "";
		}
		if (str.length() <= maxLength) {
			return str;
		}
		return str.substring(0, maxLength - 3) + "...";
	}

}
