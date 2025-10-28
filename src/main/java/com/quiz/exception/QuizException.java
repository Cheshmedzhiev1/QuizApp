package com.quiz.exception;

/**
 * Custom exception class for quiz-related errors.
 * This exception is thrown when validation fails or invalid operations are attempted.
 */
public class QuizException extends RuntimeException {

    private final String errorTitle;
    private final String errorDetails;

    /**
     * Constructor with message only
     * @param message The error message
     */
    public QuizException(String message) {
        super(message);
        this.errorTitle = "Quiz Error";
        this.errorDetails = null;
    }

    /**
     * Constructor with message and title
     * @param errorTitle The error title
     * @param message The error message
     */
    public QuizException(String errorTitle, String message) {
        super(message);
        this.errorTitle = errorTitle;
        this.errorDetails = null;
    }

    /**
     * Constructor with full details
     * @param errorTitle The error title
     * @param message The error message
     * @param errorDetails Additional error details
     */
    public QuizException(String errorTitle, String message, String errorDetails) {
        super(message);
        this.errorTitle = errorTitle;
        this.errorDetails = errorDetails;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public String getErrorDetails() {
        return errorDetails;
    }
}
