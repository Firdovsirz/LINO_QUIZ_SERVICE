package com.example.lino_quiz_service.DTO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lino_quiz_service.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    Optional<Quiz> findByQuizSerialNumber(int quizSerialNumber);
}
