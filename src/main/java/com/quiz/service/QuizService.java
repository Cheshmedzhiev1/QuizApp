package com.quiz.service;

import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.QuizResult;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class responsible for quiz manipulation and evaluation.
 * Provides functionality to randomize quiz questions and their options,
 * and evaluate user responses against correct answers.
 */
@Service
public class QuizService {

    /**
     * Shuffles both the order of questions and the order of options within each question.
     * Creates a new Quiz instance to preserve the original quiz data.
     * After shuffling options, updates the correct answer from a value (e.g., "Paris")
     * to a letter position (e.g., "C") based on where the correct answer ended up.
     */
    public Quiz shuffleQuiz(Quiz quiz) {
        // creates a copy to preserve the original one
        Quiz shuffledQuiz = new Quiz();
        shuffledQuiz.setId(quiz.getId());
        shuffledQuiz.setTitle(quiz.getTitle());
        shuffledQuiz.setDescription(quiz.getDescription());

        Set<Question> shuffledQuestions = new HashSet<>(); //list to hold shuffled questions

        for (Question originalQuestion : quiz.getQuestions()) {  // loops through each question

            Question shuffledQuestion = new Question();  // we create a copy of current question
            shuffledQuestion.setId(originalQuestion.getId());
            shuffledQuestion.setQuestion(originalQuestion.getQuestion());

            String correctAnswerValue = originalQuestion.getCorrectAnswer(); // saves 'Paris'

            Set<String> options = new HashSet<>(originalQuestion.getOptions());
            List<String> uniqueOptions = new ArrayList<>(options);
            Collections.shuffle(uniqueOptions);  // shuffles London, Paris,Berlin and Madrid
            shuffledQuestion.setOptions(uniqueOptions);

            int newIndex = uniqueOptions.indexOf(correctAnswerValue);  // finds at which index 'Paris' went

            String newCorrectLetter = String.valueOf((char) ('A' + newIndex)); //converts the index to a letter
            shuffledQuestion.setCorrectAnswer(newCorrectLetter);

            shuffledQuestions.add(shuffledQuestion); //adds the shuffled questions to new list
        }
       List<Question> questionsToShuffle = new ArrayList<>(shuffledQuestions);
        Collections.shuffle(questionsToShuffle);  //shuffles the order of questions, not answers
        shuffledQuiz.setQuestions(questionsToShuffle);  //assigns the shuffled list back to quiz

        return shuffledQuiz;
    }

    /**
     * Evaluates a quiz by comparing user-provided answers with correct answers.
     * Calculates the total score based on the number of correct responses.
     * Handles null or missing answers gracefully by treating them as incorrect.
     */
    public QuizResult evaluateQuiz(Quiz quiz, Map<Integer, String> userAnswers) {
        int score = 0;
        int totalQuestions = quiz.getQuestions().size();
// goes through each question and gets users answers , if the asnwer matches , increment the score
        for (Question question : quiz.getQuestions()) {
            String userAnswer = userAnswers.get(question.getId());  //get what the user selected
            if (userAnswer != null && userAnswer.equals(question.getCorrectAnswer())) {
                score++; // correct answer -> adds a point
            }
        }

        return new QuizResult(quiz, userAnswers, score, totalQuestions);  //cre3ates and returns quiz details
    }
}