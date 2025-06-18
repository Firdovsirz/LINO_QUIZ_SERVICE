package com.example.lino_quiz_service.DTO;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lino_quiz_service.entity.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    List<QuizQuestion> findByQuizSerialNumber(int quizSerialNumber);
}
