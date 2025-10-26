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

        List<Question> shuffledQuestions = new ArrayList<>();

        for (Question originalQuestion : quiz.getQuestions()) {

            Question shuffledQuestion = new Question();
            shuffledQuestion.setId(originalQuestion.getId());
            shuffledQuestion.setQuestion(originalQuestion.getQuestion());

            String correctAnswerValue = originalQuestion.getCorrectAnswer();

            List<String> shuffledOptions = new ArrayList<>(originalQuestion.getOptions());
            Collections.shuffle(shuffledOptions);
            shuffledQuestion.setOptions(shuffledOptions);

            int newIndex = shuffledOptions.indexOf(correctAnswerValue);

            String newCorrectLetter = String.valueOf((char) ('A' + newIndex));
            shuffledQuestion.setCorrectAnswer(newCorrectLetter);

            shuffledQuestions.add(shuffledQuestion);
        }

        Collections.shuffle(shuffledQuestions);
        shuffledQuiz.setQuestions(shuffledQuestions);

        return shuffledQuiz;
    }

    public QuizResult evaluateQuiz(Quiz quiz, Map<Integer, String> userAnswers) {
        int score = 0;
        int totalQuestions = quiz.getQuestions().size();

        for (Question question : quiz.getQuestions()) {
            String userAnswer = userAnswers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getCorrectAnswer())) {
                score++;
            }
        }

        return new QuizResult(quiz, userAnswers, score, totalQuestions);
    }
}