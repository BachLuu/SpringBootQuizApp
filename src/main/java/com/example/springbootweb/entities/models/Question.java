package com.example.springbootweb.entities.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.NonNull;

import com.example.springbootweb.entities.enums.QuestionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question {

	@Id
	@GeneratedValue(generator = "UUID")
	@UuidGenerator
	private UUID id;

	@NonNull
	@NotNull
	@Size(min = 5, max = 5000)
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@NonNull
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "question_type", nullable = false)
	private QuestionType questionType;

	@Builder.Default
	@Column(nullable = false)
	private Boolean isActive = true;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@Builder.Default
	private List<QuizQuestion> quizQuestions = new ArrayList<>();

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@Builder.Default
	private List<Answer> answers = new ArrayList<>();

	/**
	 * Danh sách các lựa chọn cho câu hỏi (A, B, C, D...). Chỉ áp dụng cho SINGLE_CHOICE
	 * và MULTIPLE_CHOICE. Có thể rỗng nếu là TRUE_FALSE hoặc LONG_ANSWER.
	 */
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@Builder.Default
	private List<QuestionOption> options = new ArrayList<>();

}