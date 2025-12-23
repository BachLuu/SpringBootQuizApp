package com.example.springbootweb.entities.models;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.NonNull;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_answers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAnswer {
    @Id
    @UuidGenerator
    private UUID id;

    @NonNull
    @NotNull
    @Column(name = "user_quiz_id", columnDefinition = "uniqueidentifier")
    private UUID userQuizId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_quiz_id", insertable = false, updatable = false)
    private UserQuiz userQuiz;

    @NonNull
    @NotNull
    @Column(name = "question_id", columnDefinition = "uniqueidentifier")
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    @Basic(fetch = FetchType.LAZY)
    @NonNull
    @NotNull
    @Column(name = "answer_id", columnDefinition = "uniqueidentifier")
    private UUID answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", insertable = false, updatable = false)
    private Answer answer;

    @Column(nullable = false)
    private Boolean isCorrect;
}