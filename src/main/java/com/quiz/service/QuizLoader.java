package com.quiz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.model.Quiz;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class QuizLoader {

    private List<Quiz> quizzes;

    public QuizLoader() {
        loadQuizzes();
    }

    private void loadQuizzes() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("data.json");
            InputStream inputStream = resource.getInputStream();

            QuizWrapper wrapper = mapper.readValue(inputStream, QuizWrapper.class);
            this.quizzes = wrapper.getQuizzes();

        } catch (IOException e) {
            e.printStackTrace();
            this.quizzes = Arrays.asList();
        }
    }

    public List<Quiz> getAllQuizzes() {
        return quizzes;
    }

    public Quiz getQuizById(int id) {
        return quizzes.stream()
                .filter(quiz -> quiz.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private static class QuizWrapper {
        private List<Quiz> quizzes;

        public List<Quiz> getQuizzes() {
            return quizzes;
        }

        public void setQuizzes(List<Quiz> quizzes) {
            this.quizzes = quizzes;
        }
    }
}