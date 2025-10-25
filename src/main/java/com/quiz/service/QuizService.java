package com.quiz.service;

import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.QuizResult;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    public Quiz shuffleQuiz(Quiz quiz) {
        Quiz shuffledQuiz = new Quiz();
        shuffledQuiz.setId(quiz.getId());
        shuffledQuiz.setTitle(quiz.getTitle());
        shuffledQuiz.setDescription(quiz.getDescription());

        List<Question> shuffledQuestions = new ArrayList<>(quiz.getQuestions());
        Collections.shuffle(shuffledQuestions);

        for (Question question : shuffledQuestions) {
            List<String> shuffledOptions = new ArrayList<>(question.getOptions());
            Collections.shuffle(shuffledOptions);
            question.setOptions(shuffledOptions);
        }

        shuffledQuiz.setQuestions(shuffledQuestions);
        return shuffledQuiz;
    }

    public QuizResult evaluateQuiz(Quiz quiz, Map<Integer, String> userAnswers) {
        int score = 0;
        int totalQuestions = quiz.getQuestions().size();

        for (Question question : quiz.getQuestions()) {
            String userAnswer = userAnswers.get(question.getId());
            if (userAnswer != null && question.isCorrectAnswer(userAnswer)) {
                score++;
            }
        }

        return new QuizResult(quiz, userAnswers, score, totalQuestions);
    }
}