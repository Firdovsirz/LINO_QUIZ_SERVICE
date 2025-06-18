package com.example.lino_quiz_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//import com.example.lino_quiz_service.DTO.CategoryRepository;
import com.example.lino_quiz_service.DTO.QuizQuestionRepository;
import com.example.lino_quiz_service.DTO.QuizRepository;
//import com.example.lino_quiz_service.entity.Category;
import com.example.lino_quiz_service.entity.Quiz;
import com.example.lino_quiz_service.entity.QuizQuestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    //add new quiz
    @PostMapping("/add")
    public ResponseEntity<?> addQuiz(@RequestBody Quiz quiz) {
        try {
            if (quiz.getQuizSerialNumber() == null || quiz.getTitle() == null || quiz.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Quiz serial number and title are required.");
            }
            
            // Check if quiz with same serial number already exists
            Optional<Quiz> existingQuiz = quizRepository.findByQuizSerialNumber(quiz.getQuizSerialNumber());
            if (existingQuiz.isPresent()) {
                return ResponseEntity.badRequest().body("Quiz with serial number " + quiz.getQuizSerialNumber() + " already exists.");
            }
            
            quizRepository.save(quiz);
            return ResponseEntity.ok("Quiz added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding quiz: " + e.getMessage());
        }
    }

    //add quiz questions
    @PostMapping("/add/{serialNumber}/questions")
    public ResponseEntity<?> addQuestions(@PathVariable int serialNumber, @RequestBody List<QuizQuestion> questions) {
        try {
            // Check if quiz exists
            Optional<Quiz> quiz = quizRepository.findByQuizSerialNumber(serialNumber);
            if (!quiz.isPresent()) {
                return ResponseEntity.badRequest().body("Quiz with serial number " + serialNumber + " not found.");
            }
            
            if (questions == null || questions.isEmpty()) {
                return ResponseEntity.badRequest().body("Questions list cannot be empty.");
            }
            
            for (QuizQuestion question : questions) {
                if (question.getQuestion() == null || question.getQuestion().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Question text is required.");
                }
                if (question.getCorrectOption() == null || question.getCorrectOption().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Correct option is required.");
                }
                
                // Set the quiz serial number for each question
                question.setQuizSerialNumber(serialNumber);
                
                // Note: Email field is not set here as it's typically set when a user takes the quiz
                // The email field in QuizQuestion seems to be for tracking which user answered which question
                // For adding questions, we don't need to set the email
                
                quizQuestionRepository.save(question);
            }
            return ResponseEntity.ok("Questions added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding questions: " + e.getMessage());
        }
    }

    //submit quiz answers
    @PostMapping("/{serialNumber}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable int serialNumber, @RequestParam String email, @RequestBody Map<String, String> answers) {
        try {
            // Check if quiz exists
            Optional<Quiz> quiz = quizRepository.findByQuizSerialNumber(serialNumber);
            if (!quiz.isPresent()) {
                return ResponseEntity.badRequest().body("Quiz with serial number " + serialNumber + " not found.");
            }
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required.");
            }
            
            if (answers == null || answers.isEmpty()) {
                return ResponseEntity.badRequest().body("Answers are required.");
            }
            
            List<QuizQuestion> questions = quizQuestionRepository.findByQuizSerialNumber(serialNumber);
            if (questions.isEmpty()) {
                return ResponseEntity.badRequest().body("No questions found for this quiz.");
            }
            
            int correctCount = 0;
            
            for (QuizQuestion question : questions) {
                String questionId = String.valueOf(question.getId());
                String userAnswer = answers.get(questionId);
                
                // Set the user's chosen option and email
                if (userAnswer != null) {
                    question.setChosenOption(userAnswer);
                    question.setEmail(email);
                    
                    // Save the updated question with user's answer
                    quizQuestionRepository.save(question);
                    
                    if (userAnswer.equalsIgnoreCase(question.getCorrectOption())) {
                        correctCount++;
                    }
                }
            }
            
            int earnedCoins = correctCount * 10; // 10 coins per correct answer
            
            Map<String, Object> response = new HashMap<>();
            response.put("correctAnswers", correctCount);
            response.put("totalQuestions", questions.size());
            response.put("earnedCoins", earnedCoins);
            response.put("percentage", (double) correctCount / questions.size() * 100);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting quiz: " + e.getMessage());
        }
    }
    
    // Get quiz questions
    @GetMapping("/{serialNumber}/questions")
    public ResponseEntity<?> getQuizQuestions(@PathVariable int serialNumber) {
        try {
            // Check if quiz exists
            Optional<Quiz> quiz = quizRepository.findByQuizSerialNumber(serialNumber);
            if (!quiz.isPresent()) {
                return ResponseEntity.badRequest().body("Quiz with serial number " + serialNumber + " not found.");
            }
            
            List<QuizQuestion> questions = quizQuestionRepository.findByQuizSerialNumber(serialNumber);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving questions: " + e.getMessage());
        }
    }
}
