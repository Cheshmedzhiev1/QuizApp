package com.quiz.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Global exception handler for the quiz application.
 * This class catches exceptions thrown by controllers and displays appropriate error pages.
 *
 * @ControllerAdvice - Allows this class to handle exceptions globally across all controllers
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles QuizException - custom exceptions thrown by quiz operations
     */
    @ExceptionHandler(QuizException.class)
    public ModelAndView handleQuizException(QuizException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error");

        // Set error attributes from the custom exception
        mav.addObject("errorTitle", ex.getErrorTitle());
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("errorDetails", ex.getErrorDetails());
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());

        return mav;
    }

    /**
     * Handles IllegalArgumentException - typically validation errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error");

        mav.addObject("errorTitle", "Invalid Input");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("errorDetails", "Please check your input and try again.");
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());

        return mav;
    }

    /**
     * Handles IllegalStateException - when application is in an invalid state
     */
    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error");

        mav.addObject("errorTitle", "Invalid Operation");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("errorDetails", "Your session may have expired. Please start over.");
        mav.addObject("status", HttpStatus.CONFLICT.value());

        return mav;
    }

    /**
     * Handles all other generic exceptions
     * This is a catch-all handler for any unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error");

        mav.addObject("errorTitle", "Unexpected Error");
        mav.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        mav.addObject("errorDetails", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        // In production, you might want to log the full stack trace here
        ex.printStackTrace();

        return mav;
    }
}
