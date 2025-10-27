package com.quiz.service;

import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.QuizResult;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    public Quiz shuffleQuiz(Quiz quiz) {
        Quiz shuffledQuiz = new Quiz(); //new quiz object to prevent modifiyng the original one
        shuffledQuiz.setId(quiz.getId());
        shuffledQuiz.setTitle(quiz.getTitle());
        shuffledQuiz.setDescription(quiz.getDescription());

        List<Question> shuffledQuestions = new ArrayList<>(); //list to hold shuffled questions

        for (Question originalQuestion : quiz.getQuestions()) {  //loops through each question

            Question shuffledQuestion = new Question();  //; creates copy of current question
            shuffledQuestion.setId(originalQuestion.getId());
            shuffledQuestion.setQuestion(originalQuestion.getQuestion());

            String correctAnswerValue = originalQuestion.getCorrectAnswer(); //stores the asnwer befoore shuffling

            List<String> shuffledOptions = new ArrayList<>(originalQuestion.getOptions()); //creates copy  and shuffles
            Collections.shuffle(shuffledOptions);
            shuffledQuestion.setOptions(shuffledOptions);

            int newIndex = shuffledOptions.indexOf(correctAnswerValue);  // finds the new index after shuffle

            String newCorrectLetter = String.valueOf((char) ('A' + newIndex)); //converts the index to a letter
            shuffledQuestion.setCorrectAnswer(newCorrectLetter);

            shuffledQuestions.add(shuffledQuestion); //adds the shuffled questions to new list
        }

        Collections.shuffle(shuffledQuestions);  //sjuffles the order of questions, not answers
        shuffledQuiz.setQuestions(shuffledQuestions);  //assigns the shuffled list back to quiz

        return shuffledQuiz;
    }

    public QuizResult evaluateQuiz(Quiz quiz, Map<Integer, String> userAnswers) {
        int score = 0;
        int totalQuestions = quiz.getQuestions().size();
// goes through each question and gets users answers , if the asnwer matches , increment the score
        for (Question question : quiz.getQuestions()) {
            String userAnswer = userAnswers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getCorrectAnswer())) {
                score++;
            }
        }

        return new QuizResult(quiz, userAnswers, score, totalQuestions);  //cre3ates and returns quiz details
    }
}