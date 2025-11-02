package com.example.SpringBootWeb.repositories;

import com.example.SpringBootWeb.entities.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    List<Quiz> findByIsActiveTrue();

    List<Quiz> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT q FROM Quiz q WHERE q.duration BETWEEN :minDuration AND :maxDuration")
    List<Quiz> findByDurationRange(@Param("minDuration") int minDuration, @Param("maxDuration") int maxDuration);
}
