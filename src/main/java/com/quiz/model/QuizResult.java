package com.quiz.model;

import java.util.Map;

/**
 * Represents the results and statistics of a completed quiz attempt
 *
 */
public class QuizResult {
    private Quiz quiz;
    private Map<Integer, String> userAnswers;
    private int score;
    private int totalQuestions;

    public QuizResult() {}

    public QuizResult(Quiz quiz, Map<Integer, String> userAnswers, int score, int totalQuestions) {
        this.quiz = quiz;
        this.userAnswers = userAnswers;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Map<Integer, String> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(Map<Integer, String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public double getPercentage() {
        if (totalQuestions == 0) return 0;
        return (score * 100.0) / totalQuestions;
    }

    public boolean isPassed() {
        return getPercentage() >= 60;
    }

    public String getMessage() {
        double percentage = getPercentage();
        if (percentage >= 90) {
            return "Excellent job !";
        } else if (percentage >= 75) {
            return "Great job !";
        } else if (percentage >= 60) {
            return "Good job !";
        } else {
            return "Still a lot to learn !";
        }
    }
}