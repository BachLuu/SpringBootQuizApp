package com.example.springbootweb.repositories;

import com.example.springbootweb.entities.enums.QuestionType;
import com.example.springbootweb.entities.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByIsActiveTrue();

    List<Question> findByContentContainingIgnoreCase(String content);

    List<Question> findByQuestionType(QuestionType questionType);
}
