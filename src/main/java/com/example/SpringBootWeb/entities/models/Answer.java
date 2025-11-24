package com.example.SpringBootWeb.entities.models;

import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "answers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Answer {
    @Id
    @UuidGenerator
    private UUID id;

    @NotNull
    @Size(min = 5, max = 5000)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Column(nullable = false)
    private Boolean isCorrect;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Basic(fetch = FetchType.LAZY)
    @NotNull
    @Column(name = "question_id", nullable = false, columnDefinition = "uniqueidentifier")
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
}
