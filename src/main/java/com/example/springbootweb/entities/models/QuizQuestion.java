package com.example.springbootweb.entities.models;

import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz_questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizQuestion {
    @Id
    @UuidGenerator
    private UUID id;

    @NotNull
    @Column(name = "quiz_id", columnDefinition = "uniqueidentifier")
    private UUID quizId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    @Basic(fetch = FetchType.LAZY)
    @NotNull
    @Column(name = "question_id", columnDefinition = "uniqueidentifier")
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    @Column(name = "`order`")
    private Integer order;
}