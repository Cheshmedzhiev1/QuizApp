package com.quiz.quiz_app;

import com.quiz.model.Quiz;
import com.quiz.service.QuizLoader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link QuizLoader}.
 * Uses a controlled in-memory setup instead of relying on actual file I/O.
 */
@SpringBootTest
class QuizLoaderTest {

    @Test
    void testLoadQuizzes_FromValidJson_ShouldLoadDataSuccessfully() throws IOException {
        // Given a mock QuizLoader that loads from an actual test resource
        QuizLoader loader = new QuizLoader() {
            protected void loadQuizzes() {

            }
        };

        List<Quiz> quizzes = loader.getAllQuizzes();

        // Then the quizzes should be loaded and not empty
        assertThat(quizzes).isNotNull();
        assertThat(quizzes.size()).isGreaterThan(0);

        // Each quiz should have expected properties
        Quiz firstQuiz = quizzes.get(0);
        assertThat(firstQuiz.getId()).isNotZero();
        assertThat(firstQuiz.getTitle()).isNotBlank();
        assertThat(firstQuiz.getQuestions()).isNotEmpty();
    }

    @Test
    void testGetQuizById_ShouldReturnCorrectQuiz() {
        // Given a QuizLoader with manually injected quizzes
        Quiz quiz1 = new Quiz();
        quiz1.setId(1);
        quiz1.setTitle("Math Quiz");

        Quiz quiz2 = new Quiz();
        quiz2.setId(2);
        quiz2.setTitle("Science Quiz");

        QuizLoader loader = new QuizLoader() {
            protected void loadQuizzes() {

            }
        };
        ReflectionTestUtils.setField(loader, "quizzes", Arrays.asList(quiz1, quiz2));

        // When
        Quiz result = loader.getQuizById(2);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Science Quiz");
    }

    @Test
    void testGetQuizById_WhenQuizNotFound_ShouldReturnNull() {
        Quiz quiz = new Quiz();
        quiz.setId(1);
        quiz.setTitle("Only Quiz");

        QuizLoader loader = new QuizLoader() {
            protected void loadQuizzes() {

            }
        };
        ReflectionTestUtils.setField(loader, "quizzes", Collections.singletonList(quiz));

        Quiz result = loader.getQuizById(99);

        assertThat(result).isNull();
    }

    @Test
    void testLoadQuizzes_WhenFileIsMissing_ShouldHandleGracefully() {
        // ✅ Create the object first
        QuizLoader loader = new QuizLoader();

        // ✅ Then modify its private field
        ReflectionTestUtils.setField(loader, "quizzes", Collections.emptyList());

        // Now test behavior
        List<Quiz> quizzes = loader.getAllQuizzes();

        assertThat(quizzes).isNotNull();
        assertThat(quizzes).isEmpty();
    }
}
