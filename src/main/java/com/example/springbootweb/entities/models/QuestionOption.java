package com.example.springbootweb.entities.models;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_options")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionOption {

	@Id
	@UuidGenerator
	private UUID id;

	@NonNull
	@NotNull
	@Column(name = "question_id", nullable = false, columnDefinition = "uniqueidentifier")
	private UUID questionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", insertable = false, updatable = false)
	private Question question;

	@NonNull
	@NotNull
	@Size(min = 1, max = 5000)
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	/**
	 * 1 -> A, 2 -> B, 3 -> C, 4 -> D...
	 */
	@NotNull
	@Column(name = "order_index", nullable = false)
	private Integer orderIndex;

	/**
	 * Dùng để chấm điểm (không trả field này về FE khi user đang làm bài).
	 */
	@NotNull
	@Column(name = "is_correct", nullable = false)
	private Boolean isCorrect;

	@Builder.Default
	@NotNull
	@Column(nullable = false)
	private Boolean isActive = true;

}