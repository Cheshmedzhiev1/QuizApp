package com.quiz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.model.Quiz;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Service class responsible for loading and managing quiz data from a JSON file.
 * This class reads quiz data from the classpath resource "data.json" and provides
 * methods to retrieve quiz information.
 */
@Service
public class QuizLoader {

    private List<Quiz> quizzes;    // list that holds of all the quizzes loaded from our json file

    public QuizLoader() {
        loadQuizzes();
    }

    /**
     * Loads quiz data from the data.json file located in the classpath.
     * Uses Jackson ObjectMapper to deserialize JSON into Quiz objects.
     * If an error occurs during loading, initializes an empty list to prevent application crash.
     */
    private void loadQuizzes() {
        try {
            ObjectMapper mapper = new ObjectMapper();   // converts between json and java objects
            ClassPathResource resource = new ClassPathResource("data.json");    // directs us to the exact chosen from us file
            InputStream inputStream = resource.getInputStream();   //opens stream to reads content of the json file

            QuizWrapper wrapper = mapper.readValue(inputStream, QuizWrapper.class);  // converts json to java
            this.quizzes = wrapper.getQuizzes();

        } catch (IOException e) {
            e.printStackTrace();
            this.quizzes = Arrays.asList();  // returns empty list to prevent app crash
        }
    }

    public List<Quiz> getAllQuizzes() {
        return quizzes;    // simply returns all the quizzes
    }

    /**
     * Retrieves a specific quiz by its ID.
     *
     * @param id the unique identifier of the quiz to retrieve
     * @return the Quiz object with the matching ID, or null if no quiz is found
     */
    public Quiz getQuizById(int id) {
        return quizzes.stream()
                .filter(quiz -> quiz.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Wrapper class used for JSON deserialization.
     * Maps the root JSON structure containing a "quizzes" array.
     */
    private static class QuizWrapper {
        // list of quizzes from the json file
        private List<Quiz> quizzes;
        // gets the list of quizzes and returns the list of Quiz Objects
        public List<Quiz> getQuizzes() {
            return quizzes;
        }
        // sets the list of quizzes
        public void setQuizzes(List<Quiz> quizzes) {
            this.quizzes = quizzes;
        }
    }
}