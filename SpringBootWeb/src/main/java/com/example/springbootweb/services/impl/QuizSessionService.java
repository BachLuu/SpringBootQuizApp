package com.example.springbootweb.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbootweb.entities.dtos.quizsessions.LeaderboardResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizQuestionResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionDetailResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionFilter;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionResultResponse;
import com.example.springbootweb.entities.dtos.quizsessions.QuizSessionSummaryResponse;
import com.example.springbootweb.entities.dtos.quizsessions.SubmitAnswerRequest;
import com.example.springbootweb.entities.dtos.quizsessions.SubmitAnswerResponse;
import com.example.springbootweb.entities.enums.QuestionType;
import com.example.springbootweb.entities.enums.QuizSessionStatus;
import com.example.springbootweb.entities.models.Answer;
import com.example.springbootweb.entities.models.Question;
import com.example.springbootweb.entities.models.Quiz;
import com.example.springbootweb.entities.models.QuizQuestion;
import com.example.springbootweb.entities.models.QuizSession;
import com.example.springbootweb.entities.models.SessionAnswer;
import com.example.springbootweb.entities.models.User;
import com.example.springbootweb.exceptions.BadRequestException;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.mappers.QuizSessionMapper;
import com.example.springbootweb.repositories.AnswerRepository;
import com.example.springbootweb.repositories.QuizRepository;
import com.example.springbootweb.repositories.QuizSessionRepository;
import com.example.springbootweb.repositories.SessionAnswerRepository;
import com.example.springbootweb.repositories.UserRepository;
import com.example.springbootweb.repositories.specifications.QuizSessionSpecifications;
import com.example.springbootweb.services.interfaces.IQuizSessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizSessionService implements IQuizSessionService {

	private static final Logger log = LoggerFactory.getLogger(QuizSessionService.class);

	private static final BigDecimal PASSING_SCORE = new BigDecimal("60.00");

	private static final BigDecimal POINTS_PER_QUESTION = BigDecimal.ONE;

	private final QuizSessionRepository quizSessionRepository;

	private final SessionAnswerRepository sessionAnswerRepository;

	private final QuizRepository quizRepository;

	private final UserRepository userRepository;

	private final AnswerRepository answerRepository;

	private final QuizSessionMapper quizSessionMapper;

	// ==================== Session Lifecycle ====================

	@Override
	@Transactional
	public QuizSessionDetailResponse startSession(UUID quizId, UUID userId) {
		log.info("Starting quiz session for quiz: {} by user: {}", quizId, userId);

		// Validate quiz exists and is active
		Quiz quiz = quizRepository.findById(quizId)
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));
		// Check if quiz is active
		if (Boolean.FALSE.equals(quiz.getIsActive())) {
			throw new BadRequestException("Quiz is not active");
		}

		// Check if user already has an active session
		if (quizSessionRepository.hasActiveSession(userId, quizId)) {
			throw new BadRequestException("You already have an active session for this quiz");
		}

		// Get questions for this quiz
		List<QuizQuestion> quizQuestions = quiz.getQuizQuestions();
		if (quizQuestions == null || quizQuestions.isEmpty()) {
			throw new BadRequestException("Quiz has no questions");
		}

		// Create new session
		LocalDateTime now = LocalDateTime.now();
		QuizSession session = QuizSession.builder()
			.userId(userId)
			.quizId(quizId)
			.status(QuizSessionStatus.IN_PROGRESS)
			.createdAt(now)
			.startedAt(now)
			.expiresAt(now.plusMinutes(quiz.getDuration()))
			.totalQuestions(quizQuestions.size())
			.maxPoints(POINTS_PER_QUESTION.multiply(BigDecimal.valueOf(quizQuestions.size())))
			.currentQuestionIndex(0)
			.build();

		QuizSession saved = quizSessionRepository.save(session);
		log.info("Created quiz session: {}", saved.getId());

		return mapToResponse(saved, quiz);
	}

	@Override
	@Transactional(readOnly = true)
	public QuizSessionDetailResponse getSession(UUID sessionId, UUID userId) {
		QuizSession session = getAndValidateSession(sessionId, userId);
		Quiz quiz = quizRepository.findById(session.getQuizId())
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
		return mapToResponse(session, quiz);
	}

	@Override
	@Transactional
	public QuizSessionDetailResponse pauseSession(UUID sessionId, UUID userId) {
		log.info("Pausing session: {}", sessionId);
		QuizSession session = getAndValidateSession(sessionId, userId);

		if (session.getStatus() != QuizSessionStatus.IN_PROGRESS) {
			throw new BadRequestException("Can only pause in-progress sessions");
		}

		// Calculate time spent so far
		int timeSpent = calculateTimeSpent(session);
		session.setTimeSpentSeconds(timeSpent);
		session.setStatus(QuizSessionStatus.PAUSED);

		QuizSession saved = quizSessionRepository.save(session);
		Quiz quiz = quizRepository.findById(session.getQuizId())
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
		return mapToResponse(saved, quiz);
	}

	@Override
	@Transactional
	public QuizSessionDetailResponse resumeSession(UUID sessionId, UUID userId) {
		log.info("Resuming session: {}", sessionId);
		QuizSession session = getAndValidateSession(sessionId, userId);

		if (session.getStatus() != QuizSessionStatus.PAUSED) {
			throw new BadRequestException("Can only resume paused sessions");
		}

		// Recalculate expiry time based on remaining time
		Quiz quiz = quizRepository.findById(session.getQuizId())
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

		int totalSeconds = quiz.getDuration() * 60;
		int remainingSeconds = totalSeconds - session.getTimeSpentSeconds();
		session.setExpiresAt(LocalDateTime.now().plusSeconds(remainingSeconds));
		session.setStatus(QuizSessionStatus.IN_PROGRESS);

		QuizSession saved = quizSessionRepository.save(session);
		return mapToResponse(saved, quiz);
	}

	@Override
	@Transactional
	public void abandonSession(UUID sessionId, UUID userId) {
		log.info("Abandoning session: {}", sessionId);
		QuizSession session = getAndValidateSession(sessionId, userId);

		if (session.getStatus() == QuizSessionStatus.SUBMITTED || session.getStatus() == QuizSessionStatus.GRADED) {
			throw new BadRequestException("Cannot abandon a completed session");
		}

		session.setStatus(QuizSessionStatus.ABANDONED);
		session.setFinishedAt(LocalDateTime.now());
		quizSessionRepository.save(session);
	}

	// ==================== Question & Answer ====================

	@Override
	@Transactional(readOnly = true)
	public QuizQuestionResponse getCurrentQuestion(UUID sessionId, UUID userId) {
		QuizSession session = getAndValidateSession(sessionId, userId);
		validateSessionInProgress(session);
		return getQuestionByIndex(sessionId, session.getCurrentQuestionIndex(), userId);
	}

	@Override
	@Transactional(readOnly = true)
	public QuizQuestionResponse getQuestionByIndex(UUID sessionId, int questionIndex, UUID userId) {
		QuizSession session = getAndValidateSession(sessionId, userId);
		validateSessionInProgress(session);

		Quiz quiz = quizRepository.findById(session.getQuizId())
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

		List<QuizQuestion> quizQuestions = quiz.getQuizQuestions();
		if (questionIndex < 0 || questionIndex >= quizQuestions.size()) {
			throw new BadRequestException("Invalid question index");
		}

		Question question = quizQuestions.get(questionIndex).getQuestion();

		// Check if already answered
		SessionAnswer existingAnswer = sessionAnswerRepository
			.findByQuizSessionIdAndQuestionId(sessionId, question.getId())
			.orElse(null);

		// Map answers (without revealing correct answer)
		List<QuizQuestionResponse.QuizAnswerOption> options = question.getAnswers()
			.stream()
			.filter(Answer::getIsActive)
			.map(a -> new QuizQuestionResponse.QuizAnswerOption(a.getId(), a.getContent()))
			.toList();

		return new QuizQuestionResponse(question.getId(), question.getContent(), question.getQuestionType(),
				questionIndex + 1, session.getTotalQuestions(), options, existingAnswer != null,
				existingAnswer != null ? existingAnswer.getAnswerId() : null,
				existingAnswer != null ? existingAnswer.getTextResponse() : null);
	}

	@Override
	@Transactional
	public SubmitAnswerResponse submitAnswer(UUID sessionId, SubmitAnswerRequest request, UUID userId) {
		log.info("Submitting answer for session: {}, question: {}", sessionId, request.questionId());
		QuizSession session = getAndValidateSession(sessionId, userId);
		validateSessionInProgress(session);
		checkSessionExpiry(session);

		// Validate question belongs to this quiz
		Quiz quiz = quizRepository.findById(session.getQuizId())
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

		Question question = quiz.getQuizQuestions()
			.stream()
			.map(QuizQuestion::getQuestion)
			.filter(q -> q.getId().equals(request.questionId()))
			.findFirst()
			.orElseThrow(() -> new BadRequestException("Question not part of this quiz"));

		// Check if already answered - update if so
		SessionAnswer existingAnswer = sessionAnswerRepository
			.findByQuizSessionIdAndQuestionId(sessionId, request.questionId())
			.orElse(null);

		boolean isNewAnswer = existingAnswer == null;
		SessionAnswer sessionAnswer = existingAnswer != null ? existingAnswer : new SessionAnswer();

		// Determine if answer is correct
		Boolean isCorrect = null;
		BigDecimal pointsAwarded = BigDecimal.ZERO;

		if (isAutoGradable(question.getQuestionType()) && request.answerId() != null) {
			Answer selectedAnswer = answerRepository.findById(request.answerId())
				.orElseThrow(() -> new BadRequestException("Invalid answer ID"));
			isCorrect = selectedAnswer.getIsCorrect();
			pointsAwarded = Boolean.TRUE.equals(isCorrect) ? POINTS_PER_QUESTION : BigDecimal.ZERO;
		}
		// For text-based questions, isCorrect remains null until manual review

		// Update or create session answer
		sessionAnswer.setQuizSessionId(sessionId);
		sessionAnswer.setQuestionId(request.questionId());
		sessionAnswer.setAnswerId(request.answerId());
		sessionAnswer.setTextResponse(request.textResponse());
		sessionAnswer.setIsCorrect(isCorrect);
		sessionAnswer.setPointsAwarded(pointsAwarded);
		sessionAnswer.setAnsweredAt(LocalDateTime.now());
		sessionAnswer.setTimeSpentSeconds(request.timeSpentSeconds() != null ? request.timeSpentSeconds() : 0);
		sessionAnswer.setAnswerOrder(session.getAnsweredQuestions() + (isNewAnswer ? 1 : 0));

		sessionAnswerRepository.save(sessionAnswer);

		// Update session progress
		if (isNewAnswer) {
			session.setAnsweredQuestions(session.getAnsweredQuestions() + 1);
			if (Boolean.TRUE.equals(isCorrect)) {
				session.setCorrectAnswers(session.getCorrectAnswers() + 1);
				session.setPointsEarned(session.getPointsEarned().add(pointsAwarded));
			}
		}
		else if (isCorrect != null) {
			// Recalculate if answer changed
			recalculateSessionScore(session);
		}

		quizSessionRepository.save(session);

		// Calculate remaining time
		int remainingTime = calculateRemainingTime(session);

		// Build feedback message
		String feedbackMessage = buildFeedbackMessage(isCorrect);

		return new SubmitAnswerResponse(true, isCorrect, pointsAwarded, session.getAnsweredQuestions(),
				session.getTotalQuestions(), remainingTime, feedbackMessage);
	}

	private String buildFeedbackMessage(Boolean isCorrect) {
		if (isCorrect == null) {
			return "Answer submitted for review";
		}
		return Boolean.TRUE.equals(isCorrect) ? "Correct!" : "Incorrect";
	}

	@Override
	@Transactional
	public QuizQuestionResponse nextQuestion(UUID sessionId, UUID userId) {
		QuizSession session = getAndValidateSession(sessionId, userId);
		validateSessionInProgress(session);

		int nextIndex = session.getCurrentQuestionIndex() + 1;
		if (nextIndex >= session.getTotalQuestions()) {
			throw new BadRequestException("No more questions");
		}

		session.setCurrentQuestionIndex(nextIndex);
		quizSessionRepository.save(session);

		return getQuestionByIndex(sessionId, nextIndex, userId);
	}

	@Override
	@Transactional
	public QuizQuestionResponse previousQuestion(UUID sessionId, UUID userId) {
		QuizSession session = getAndValidateSession(sessionId, userId);
		validateSessionInProgress(session);

		int prevIndex = session.getCurrentQuestionIndex() - 1;
		if (prevIndex < 0) {
			throw new BadRequestException("Already at first question");
		}

		session.setCurrentQuestionIndex(prevIndex);
		quizSessionRepository.save(session);

		return getQuestionByIndex(sessionId, prevIndex, userId);
	}

	// ==================== Submission & Results ====================

	@Override
	@Transactional
	public QuizSessionResultResponse submitQuiz(UUID sessionId, UUID userId) {
		log.info("Submitting quiz session: {}", sessionId);
		QuizSession session = getAndValidateSession(sessionId, userId);

		if (session.getStatus() != QuizSessionStatus.IN_PROGRESS && session.getStatus() != QuizSessionStatus.PAUSED) {
			throw new BadRequestException("Session cannot be submitted");
		}

		// Calculate final results
		calculateFinalScore(session);
		session.setStatus(QuizSessionStatus.SUBMITTED);
		session.setFinishedAt(LocalDateTime.now());
		session.setTimeSpentSeconds(calculateTimeSpent(session));

		// Determine pass/fail
		session.setIsPassed(session.getScore().compareTo(PASSING_SCORE) >= 0);

		quizSessionRepository.save(session);
		log.info("Quiz submitted. Score: {}, Passed: {}", session.getScore(), session.getIsPassed());

		return getResult(sessionId, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public QuizSessionResultResponse getResult(UUID sessionId, UUID userId) {
		QuizSession session = getAndValidateSession(sessionId, userId);

		if (session.getStatus() != QuizSessionStatus.SUBMITTED && session.getStatus() != QuizSessionStatus.GRADED
				&& session.getStatus() != QuizSessionStatus.TIMED_OUT) {
			throw new BadRequestException("Results not available yet");
		}

		Quiz quiz = quizRepository.findById(session.getQuizId())
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

		User user = userRepository.findById(session.getUserId())
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Get all answers with details
		List<SessionAnswer> sessionAnswers = sessionAnswerRepository.findByQuizSessionIdOrderByAnswerOrder(sessionId);

		List<QuizSessionResultResponse.AnswerResultResponse> answerResults = new ArrayList<>();

		for (QuizQuestion qq : quiz.getQuizQuestions()) {
			Question question = qq.getQuestion();
			SessionAnswer sa = sessionAnswers.stream()
				.filter(a -> a.getQuestionId().equals(question.getId()))
				.findFirst()
				.orElse(null);

			// Find correct answer
			Answer correctAnswer = question.getAnswers()
				.stream()
				.filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
				.findFirst()
				.orElse(null);

			// Find selected answer
			Answer selectedAnswer = sa != null && sa.getAnswerId() != null ? question.getAnswers()
				.stream()
				.filter(a -> a.getId().equals(sa.getAnswerId()))
				.findFirst()
				.orElse(null) : null;

			// All options
			List<QuizSessionResultResponse.AnswerOption> allOptions = question.getAnswers()
				.stream()
				.filter(Answer::getIsActive)
				.map(a -> new QuizSessionResultResponse.AnswerOption(a.getId(), a.getContent(), a.getIsCorrect(),
						sa != null && a.getId().equals(sa.getAnswerId())))
				.toList();

			answerResults.add(new QuizSessionResultResponse.AnswerResultResponse(question.getId(),
					question.getContent(), question.getQuestionType().name(), sa != null ? sa.getAnswerId() : null,
					selectedAnswer != null ? selectedAnswer.getContent() : null,
					correctAnswer != null ? correctAnswer.getId() : null,
					correctAnswer != null ? correctAnswer.getContent() : null, sa != null ? sa.getTextResponse() : null,
					sa != null && Boolean.TRUE.equals(sa.getIsCorrect()),
					sa != null ? sa.getPointsAwarded() : BigDecimal.ZERO, POINTS_PER_QUESTION,
					sa != null ? sa.getTimeSpentSeconds() : 0, sa != null && Boolean.TRUE.equals(sa.getIsReviewed()),
					sa != null ? sa.getReviewerFeedback() : null, allOptions));
		}

		// Calculate statistics
		int rank = quizSessionRepository.getUserRank(quiz.getId(), session.getScore(), session.getTimeSpentSeconds());
		long totalParticipants = quizSessionRepository.countCompletedSessions(quiz.getId());

		int avgTimePerQuestion = session.getTotalQuestions() > 0
				? session.getTimeSpentSeconds() / session.getTotalQuestions() : 0;

		QuizSessionResultResponse.QuizStatistics statistics = new QuizSessionResultResponse.QuizStatistics(
				session.getTotalQuestions() > 0
						? (double) session.getCorrectAnswers() / session.getTotalQuestions() * 100 : 0,
				avgTimePerQuestion, null, // Could calculate fastest/slowest
				null, rank, (int) totalParticipants);

		return new QuizSessionResultResponse(session.getId(), quiz.getId(), quiz.getTitle(), quiz.getDescription(),
				user.getId(), user.getDisplayName(), session.getStatus(), session.getStartedAt(),
				session.getFinishedAt(), session.getTimeSpentSeconds(), session.getTotalQuestions(),
				session.getAnsweredQuestions(), session.getCorrectAnswers(),
				session.getTotalQuestions() - session.getCorrectAnswers(),
				session.getTotalQuestions() - session.getAnsweredQuestions(), session.getScore(),
				session.getPointsEarned(), session.getMaxPoints(), session.getIsPassed(), PASSING_SCORE, answerResults,
				statistics);
	}

	// ==================== History & Leaderboard ====================

	@Override
	@Transactional(readOnly = true)
	public List<QuizSessionSummaryResponse> getUserHistory(UUID userId, QuizSessionFilter quizSessionFilter) {

		// Build Specification from filter
		Specification<QuizSession> spec = QuizSessionSpecifications.fromFilter(quizSessionFilter);

		return quizSessionRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"))
			.stream()
			.map(this::mapToSummary)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<QuizSessionSummaryResponse> getUserHistory(UUID userId, int page, int size,
			QuizSessionFilter quizSessionFilter) {

		// Build Specification from filter
		Specification<QuizSession> spec = QuizSessionSpecifications.fromFilter(quizSessionFilter);
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		return quizSessionRepository.findAll(spec, pageable).map(this::mapToSummary);
	}

	@Override
	@Transactional(readOnly = true)
	public LeaderboardResponse getLeaderboard(UUID quizId, int limit) {
		Quiz quiz = quizRepository.findById(quizId)
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));

		Pageable pageable = PageRequest.of(0, limit);
		Page<QuizSession> sessionsPage = quizSessionRepository.findLeaderboard(quizId, pageable);

		return buildLeaderboard(quiz, sessionsPage.getContent());
	}

	@Override
	@Transactional(readOnly = true)
	public LeaderboardResponse getLeaderboard(UUID quizId, int page, int size) {
		Quiz quiz = quizRepository.findById(quizId)
			.orElseThrow(() -> new ResourceNotFoundException("Quiz not found: " + quizId));

		Pageable pageable = PageRequest.of(page, size);
		Page<QuizSession> sessionsPage = quizSessionRepository.findLeaderboard(quizId, pageable);

		return buildLeaderboard(quiz, sessionsPage.getContent());
	}

	// ==================== Admin/System Operations ====================

	@Override
	@Transactional
	public void processExpiredSessions() {
		log.info("Processing expired sessions");
		List<QuizSession> expiredSessions = quizSessionRepository.findExpiredSessions(LocalDateTime.now());

		for (QuizSession session : expiredSessions) {
			try {
				calculateFinalScore(session);
				session.setStatus(QuizSessionStatus.TIMED_OUT);
				session.setFinishedAt(LocalDateTime.now());
				session.setIsPassed(session.getScore().compareTo(PASSING_SCORE) >= 0);
				quizSessionRepository.save(session);
				log.info("Auto-submitted expired session: {}", session.getId());
			}
			catch (Exception e) {
				log.error("Error processing expired session: {}", session.getId(), e);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public boolean canStartSession(UUID quizId, UUID userId) {
		return !quizSessionRepository.hasActiveSession(userId, quizId);
	}

	// ==================== Helper Methods ====================

	private QuizSession getAndValidateSession(UUID sessionId, UUID userId) {
		QuizSession session = quizSessionRepository.findById(sessionId)
			.orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

		if (!session.getUserId().equals(userId)) {
			throw new BadRequestException("You don't have access to this session");
		}

		return session;
	}

	private void validateSessionInProgress(QuizSession session) {
		if (session.getStatus() != QuizSessionStatus.IN_PROGRESS) {
			throw new BadRequestException("Session is not in progress");
		}
	}

	private void checkSessionExpiry(QuizSession session) {
		if (session.getExpiresAt() != null && LocalDateTime.now().isAfter(session.getExpiresAt())) {
			session.setStatus(QuizSessionStatus.TIMED_OUT);
			quizSessionRepository.save(session);
			throw new BadRequestException("Session has expired");
		}
	}

	private boolean isAutoGradable(QuestionType type) {
		return type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE
				|| type == QuestionType.TRUE_FALSE;
	}

	private int calculateTimeSpent(QuizSession session) {
		if (session.getStartedAt() == null) {
			return session.getTimeSpentSeconds();
		}
		LocalDateTime endTime = session.getFinishedAt() != null ? session.getFinishedAt() : LocalDateTime.now();
		return (int) ChronoUnit.SECONDS.between(session.getStartedAt(), endTime);
	}

	private int calculateRemainingTime(QuizSession session) {
		if (session.getExpiresAt() == null) {
			return 0;
		}
		long remaining = ChronoUnit.SECONDS.between(LocalDateTime.now(), session.getExpiresAt());
		return Math.max(0, (int) remaining);
	}

	private void calculateFinalScore(QuizSession session) {
		List<SessionAnswer> answers = sessionAnswerRepository.findByQuizSessionIdOrderByAnswerOrder(session.getId());

		int correct = 0;
		BigDecimal pointsEarned = BigDecimal.ZERO;

		for (SessionAnswer answer : answers) {
			if (Boolean.TRUE.equals(answer.getIsCorrect())) {
				correct++;
				pointsEarned = pointsEarned
					.add(answer.getPointsAwarded() != null ? answer.getPointsAwarded() : BigDecimal.ZERO);
			}
		}

		session.setCorrectAnswers(correct);
		session.setAnsweredQuestions(answers.size());
		session.setPointsEarned(pointsEarned);

		// Calculate percentage score
		if (session.getTotalQuestions() > 0) {
			BigDecimal score = BigDecimal.valueOf(correct)
				.divide(BigDecimal.valueOf(session.getTotalQuestions()), 4, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100))
				.setScale(2, RoundingMode.HALF_UP);
			session.setScore(score);
		}
	}

	private void recalculateSessionScore(QuizSession session) {
		calculateFinalScore(session);
	}

	/**
	 * Map QuizSession to QuizSessionDetailResponse using MapStruct.
	 */
	private QuizSessionDetailResponse mapToResponse(QuizSession session, Quiz quiz) {
		User user = userRepository.findById(session.getUserId()).orElse(null);
		return quizSessionMapper.toDetailResponse(session, quiz, user, calculateRemainingTime(session));
	}

	/**
	 * Map QuizSession to QuizSessionSummaryResponse using MapStruct.
	 */
	private QuizSessionSummaryResponse mapToSummary(QuizSession session) {
		Quiz quiz = quizRepository.findById(session.getQuizId()).orElse(null);
		return quizSessionMapper.toSummaryResponse(session, quiz);
	}

	/**
	 * Build leaderboard response using MapStruct.
	 */
	private LeaderboardResponse buildLeaderboard(Quiz quiz, List<QuizSession> sessions) {
		AtomicInteger rank = new AtomicInteger(1);

		List<LeaderboardResponse.LeaderboardEntry> entries = sessions.stream().map(session -> {
			User user = userRepository.findById(session.getUserId()).orElse(null);
			return quizSessionMapper.toLeaderboardEntry(session, user, rank.getAndIncrement());
		}).toList();

		return quizSessionMapper.toLeaderboardResponse(quiz, entries);
	}

}
