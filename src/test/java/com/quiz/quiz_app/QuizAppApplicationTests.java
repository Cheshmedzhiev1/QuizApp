package com.quiz.quiz_app;

import com.quiz.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.QuizResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuizAppApplicationTests {

    @Autowired
    private QuizService quizService;

    private Quiz sampleQuiz;

    @BeforeEach
    void setUp() {
        // create sample questions
        Question q1 = new Question();
        q1.setId(1);
        q1.setQuestion("What is the capital of France?");
        q1.setOptions(Arrays.asList("Paris", "London", "Berlin", "Madrid"));
        q1.setCorrectAnswer("Paris");

        Question q2 = new Question();
        q2.setId(2);
        q2.setQuestion("What is 2 + 2?");
        q2.setOptions(Arrays.asList("3", "4", "5", "6"));
        q2.setCorrectAnswer("4");

        // create quiz
        sampleQuiz = new Quiz();
        sampleQuiz.setId(101);
        sampleQuiz.setTitle("General Knowledge");
        sampleQuiz.setDescription("Simple quiz for testing");
        sampleQuiz.setQuestions(Arrays.asList(q1, q2));
    }

    @Test
    void testShuffleQuiz_ShouldRandomizeQuestionsAndOptions() {
        Quiz shuffled = quizService.shuffleQuiz(sampleQuiz);

        assertThat(shuffled).isNotNull();
        assertThat(shuffled.getQuestions()).hasSize(2);

        // ensure it's a different object
        assertThat(shuffled).isNotSameAs(sampleQuiz);

        // verify correctAnswer is a letter (A, B, C, D)
        for (Question q : shuffled.getQuestions()) {
            String answer = q.getCorrectAnswer();
            assertThat(answer).matches("[A-D]");
        }

        // ensure options are shuffled but still contain same elements
        for (int i = 0; i < sampleQuiz.getQuestions().size(); i++) {
            Question original = sampleQuiz.getQuestions().get(i);
            Question shuffledQ = shuffled.getQuestions().stream()
                    .filter(q -> q.getId() == original.getId())
                    .findFirst()
                    .orElse(null);

            assertThat(shuffledQ).isNotNull();
            assertThat(new HashSet<>(shuffledQ.getOptions()))
                    .containsExactlyInAnyOrderElementsOf(original.getOptions());
        }
    }

    @Test
    void testEvaluateQuiz_AllCorrectAnswers_ShouldReturnFullScore() {
        // shuffle first to simulate realistic scenario
        Quiz shuffled = quizService.shuffleQuiz(sampleQuiz);

        Map<Integer, String> userAnswers = new HashMap<>();
        for (Question q : shuffled.getQuestions()) {
            userAnswers.put(q.getId(), q.getCorrectAnswer());
        }

        QuizResult result = quizService.evaluateQuiz(shuffled, userAnswers);

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(result.getTotalQuestions());
    }

    @Test
    void testEvaluateQuiz_SomeIncorrectAnswers_ShouldReturnPartialScore() {
        Quiz shuffled = quizService.shuffleQuiz(sampleQuiz);

        Map<Integer, String> userAnswers = new HashMap<>();
        for (Question q : shuffled.getQuestions()) {
            if (q.getId() == 1) {
                userAnswers.put(q.getId(), q.getCorrectAnswer()); // correct
            } else {
                userAnswers.put(q.getId(), "Z"); // wrong
            }
        }

        QuizResult result = quizService.evaluateQuiz(shuffled, userAnswers);

        assertThat(result.getScore()).isEqualTo(1);
        assertThat(result.getTotalQuestions()).isEqualTo(2);
    }

    @Test
    void testEvaluateQuiz_MissingAnswers_ShouldHandleGracefully() {
        Quiz shuffled = quizService.shuffleQuiz(sampleQuiz);

        Map<Integer, String> userAnswers = new HashMap<>();
        userAnswers.put(1, shuffled.getQuestions().get(0).getCorrectAnswer()); // only one answer

        QuizResult result = quizService.evaluateQuiz(shuffled, userAnswers);

        assertThat(result.getScore()).isLessThan(result.getTotalQuestions());
        assertThat(result.getTotalQuestions()).isEqualTo(2);
    }

    @Test
    void contextLoads() {
    }

}
