package com.example.SpringBootWeb.repositories;

import com.example.SpringBootWeb.entities.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByIsActiveTrue();

    List<Answer> findByContentContainingIgnoreCase(String content);

    List<Answer> findByQuestionId(UUID questionId);
}
