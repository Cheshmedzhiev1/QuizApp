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

/**
 * Unit tests for QuizService functionality.
 * <p>
 * Tests the core quiz operations: shuffling questions/options and evaluating user answers.
 * Uses @SpringBootTest to load the full application context with real dependencies.
 */
@SpringBootTest  // Loads full Spring context - useful for integration testing
class QuizAppApplicationTests {

    @Autowired  // Injects the real QuizService bean (not a mock)
    private QuizService quizService;

    private Quiz sampleQuiz;  // Reusable test data created before each test

    /**
     * Setup method that runs before EACH test.
     * <p>
     * Creates a fresh quiz with 2 questions:
     * 1. "What is the capital of France?" (Answer: Paris)
     * 2. "What is 2 + 2?" (Answer: 4)
     * <p>
     * This ensures each test starts with clean, consistent data.
     */
    @BeforeEach
    void setUp() {
        // Create Question 1: Geography question with 4 options
        Question q1 = new Question();
        q1.setId(1);
        q1.setQuestion("What is the capital of France?");
        q1.setOptions(Arrays.asList("Paris", "London", "Berlin", "Madrid"));
        q1.setCorrectAnswer("Paris");  // Answer is the actual text

        // Create Question 2: Math question with 4 options
        Question q2 = new Question();
        q2.setId(2);
        q2.setQuestion("What is 2 + 2?");
        q2.setOptions(Arrays.asList("3", "4", "5", "6"));
        q2.setCorrectAnswer("4");  // Answer is the actual text

        // Create quiz containing both questions
        sampleQuiz = new Quiz();
        sampleQuiz.setId(101);
        sampleQuiz.setTitle("General Knowledge");
        sampleQuiz.setDescription("Simple quiz for testing");
        sampleQuiz.setQuestions(Arrays.asList(q1, q2));
    }

    /**
     * TEST 1: Verifies that shuffleQuiz() properly randomizes questions and options.
     * <p>
     * How it works:
     * 1. Takes the original quiz
     * 2. Calls shuffleQuiz() which randomizes question order AND option order
     * 3. Converts answers from text (e.g., "Paris") to letters (e.g., "A", "B", "C", "D")
     * 4. Verifies the shuffled quiz is a new object (not modifying the original)
     * 5. Checks that correct answers are now letters (A-D format)
     * 6. Ensures all original options are still present (just reordered)
     */
    @Test
    void testShuffleQuiz_ShouldRandomizeQuestionsAndOptions() {
        // Execute: Call the shuffle method
        Quiz shuffled = quizService.shuffleQuiz(sampleQuiz);

        // Verify: Quiz exists and has same number of questions
        assertThat(shuffled).isNotNull();
        assertThat(shuffled.getQuestions()).hasSize(2);

        // Verify: It's a new object (defensive copy), not modifying original
        assertThat(shuffled).isNotSameAs(sampleQuiz);

        // Verify: Correct answers are converted to letter format (A, B, C, or D)
        // This is important because after shuffling, answers reference positions
        for (Question q : shuffled.getQuestions()) {
            String answer = q.getCorrectAnswer();
            assertThat(answer).matches("[A-D]");  // Must be A, B, C, or D
        }

        // Verify: Options are shuffled but no options are lost or added
        // Check that each shuffled question contains the same options (just reordered)
        for (int i = 0; i < sampleQuiz.getQuestions().size(); i++) {
            Question original = sampleQuiz.getQuestions().get(i);

            // Find the corresponding shuffled question by ID
            Question shuffledQ = shuffled.getQuestions().stream()
                    .filter(q -> q.getId() == original.getId())
                    .findFirst()
                    .orElse(null);

            assertThat(shuffledQ).isNotNull();

            // Verify shuffled options contain same elements (using HashSet ignores order)
            assertThat(new HashSet<>(shuffledQ.getOptions()))
                    .containsExactlyInAnyOrderElementsOf(original.getOptions());
        }
    }

    /**
     * TEST 2: Verifies that evaluateQuiz() returns full score when all answers are correct.
     * <p>
     * How it works:
     * 1. Shuffles the quiz (simulates real user experience)
     * 2. Creates a map of user answers where ALL answers are correct
     * 3. Calls evaluateQuiz() to grade the quiz
     * 4. Verifies that score equals total questions (perfect score: 2/2)
     */
    @Test
    void testEvaluateQuiz_AllCorrectAnswers_ShouldReturnFullScore() {
        // Setup: Shuffle quiz first (realistic scenario)
        Quiz shuffled = quizService.shuffleQuiz(sampleQuiz);

        // Setup: Create user answers map with ALL CORRECT answers
        Map<Integer, String> userAnswers = new HashMap<>();
        for (Question q : shuffled.getQuestions()) {
            // Put correct answer for each question
            userAnswers.put(q.getId(), q.getCorrectAnswer());
        }

        // Execute: Evaluate the quiz
        QuizResult result = quizService.evaluateQuiz(shuffled, userAnswers);

        // Verify: Result exists
        assertThat(result).isNotNull();

        // Verify: Perfect score - all questions answered correctly
        assertThat(result.getScore()).isEqualTo(result.getTotalQuestions());
        // Expected: score = 2, totalQuestions = 2
    }

    /**
     * TEST 3: Verifies partial score calculation with mixed correct/incorrect answers.
     * <p>
     * How it works:
     * 1. Shuffles the quiz
     * 2. Creates answers where Question 1 is CORRECT and Question 2 is WRONG
     * 3. Evaluates the quiz
     * 4. Verifies score is 1 out of 2 (50% correct)
     */
    @Test
    void testEvaluateQuiz_SomeIncorrectAnswers_ShouldReturnPartialScore() {
        // Setup: Shuffle quiz
        Quiz shuffled = quizService.shuffleQuiz(sampleQuiz);

        // Setup: Create mixed answers (one correct, one wrong)
        Map<Integer, String> userAnswers = new HashMap<>();
        for (Question q : shuffled.getQuestions()) {
            if (q.getId() == 1) {
                // Question 1: CORRECT answer
                userAnswers.put(q.getId(), q.getCorrectAnswer());
            } else {
                // Question 2: WRONG answer (Z is invalid)
                userAnswers.put(q.getId(), "Z");
            }
        }

        // Execute: Evaluate quiz
        QuizResult result = quizService.evaluateQuiz(shuffled, userAnswers);

        // Verify: Partial score - only 1 out of 2 correct
        assertThat(result.getScore()).isEqualTo(1);
        assertThat(result.getTotalQuestions()).isEqualTo(2);
        // Expected result: 50% score
    }

    /**
     * TEST 4: Verifies graceful handling when user doesn't answer all questions.
     * <p>
     * How it works:
     * 1. Shuffles the quiz
     * 2. User only answers 1 question (skips the other)
     * 3. Evaluates incomplete quiz
     * 4. Verifies score is less than total questions and doesn't crash
     * <p>
     * This tests that the system handles missing answers without errors.
     */
    @Test
    void testEvaluateQuiz_MissingAnswers_ShouldHandleGracefully() {
        // Setup: Shuffle quiz
        Quiz shuffled = quizService.shuffleQuiz(sampleQuiz);

        // Setup: User only answers ONE question (incomplete)
        Map<Integer, String> userAnswers = new HashMap<>();
        userAnswers.put(1, shuffled.getQuestions().get(0).getCorrectAnswer());
        // Note: Question 2 is NOT answered (missing from map)

        // Execute: Evaluate quiz with missing answer
        QuizResult result = quizService.evaluateQuiz(shuffled, userAnswers);

        // Verify: Score is less than total (since one question wasn't answered)
        assertThat(result.getScore()).isLessThan(result.getTotalQuestions());

        // Verify: Total questions count is still correct
        assertThat(result.getTotalQuestions()).isEqualTo(2);

        // Expected: score = 1 (only answered question), totalQuestions = 2
        // This proves unanswered questions don't cause crashes
    }

    /**
     * TEST 5: Basic Spring Boot context loading test.
     * <p>
     * How it works:
     * Simply verifies that the Spring application context loads successfully.
     * If this fails, there's a configuration problem with the app.
     * This is automatically generated by Spring Initializr.
     */
    @Test
    void contextLoads() {
        // Empty test - just verifies Spring context starts without errors
        // If Spring configuration is broken, this test will fail
    }
}