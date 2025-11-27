package com.example.SpringBootWeb.repositories;

import com.example.SpringBootWeb.entities.enums.QuestionType;
import com.example.SpringBootWeb.entities.models.Question;
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
