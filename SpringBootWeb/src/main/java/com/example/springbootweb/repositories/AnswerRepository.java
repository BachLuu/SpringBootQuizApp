package com.example.springbootweb.repositories;

import com.example.springbootweb.entities.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID>, JpaSpecificationExecutor<Answer> {
    List<Answer> findByIsActiveTrue();

    List<Answer> findByContentContainingIgnoreCase(String content);

    List<Answer> findByQuestionId(UUID questionId);
}
