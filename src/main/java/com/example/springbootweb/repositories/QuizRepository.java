package com.example.springbootweb.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbootweb.entities.models.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

	List<Quiz> findByIsActiveTrue();

	List<Quiz> findByTitleContainingIgnoreCase(String title);

	@Query("SELECT q FROM Quiz q WHERE q.duration BETWEEN :minDuration AND :maxDuration")
	List<Quiz> findByDurationRange(@Param("minDuration") int minDuration, @Param("maxDuration") int maxDuration);

}
