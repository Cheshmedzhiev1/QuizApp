package com.quiz.controller;

import com.quiz.exception.QuizException;
import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.QuizResult;
import com.quiz.service.QuizLoader;
import com.quiz.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller class responsible for managing quiz-related HTTP request and user interactions
 * Handles the complete quiz lifecycle
 * Starting a new quiz session
 * Displays all the question
 * Records the user answers
 * Navigates between question ( back and forth)
 * Submits quiz for evaluation
 * Retaking quiz
 */
@Controller
@RequestMapping("/quiz")
public class QuizController {

    /**
     * Service component for loading quiz data from the data source.
     * Injected via Spring's dependency injection.
     */
    @Autowired
    private QuizLoader quizLoader;

    /**
     * Service component for quiz business logic operations such as
     * shuffling questions and evaluating quiz results.
     * Injected via Spring's dependency injection.
     */
    @Autowired
    private QuizService quizService;


    /**
     * Initializes a new quiz session for the specified quiz ID.
     * This method performs the following operations:
     * Retrieves the quiz by ID from the data source
     * Validates quiz existence
     * Shuffles questions for randomization
     * Initializes session attributes for quiz state management
     * Redirects to the first question
     */
    @GetMapping("/start/{quizId}")
    public String startQuiz(@PathVariable int quizId, HttpSession session) {
        // Validate quiz ID is positive
        if (quizId <= 0) {
            throw new QuizException(
                "Invalid Quiz ID",
                "The quiz ID must be a positive number.",
                "Received quiz ID: " + quizId
            );
        }

        Quiz quiz = quizLoader.getQuizById(quizId);  //retrieves us the quiz by the id
        if (quiz == null) {    // if quiz does not exist, throw exception
            throw new QuizException(
                "Quiz Not Found",
                "The requested quiz could not be found.",
                "Quiz ID " + quizId + " does not exist in the system."
            );
        }

        // Validate quiz has questions
        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            throw new QuizException(
                "Invalid Quiz",
                "This quiz has no questions.",
                "Quiz ID: " + quizId
            );
        }

        Quiz shuffledQuiz = quizService.shuffleQuiz(quiz);   // we shuffle the question to any random order

        // Initialize session state for the new quiz attempt
        // Store the shuffled quiz for use throughout the session
        session.setAttribute("currentQuiz", shuffledQuiz);
        // Set question index to 0 to start from the first question
        session.setAttribute("currentQuestionIndex", 0);
        // Initialize empty map to store user's answers (questionId -> answer)
        session.setAttribute("userAnswers", new HashMap<Integer, String>());

        return "redirect:/quiz/question";  //brings us back to the first question
    }

    /**
     * Displays the current question in the quiz sequence.
     * <p>
     * This method retrieves the quiz state from the session and displays
     * the appropriate question based on the current index. It handles three scenarios:
     * Invalid session - redirects to home
     * All questions completed - redirects to submit
     * Valid question index - displays the question
     */
    @GetMapping("/question")
    public String showQuestion(HttpSession session, Model model) {
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");  // retrieves quiz and current question index from current session
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");

        // Validate session state
        if (quiz == null || currentIndex == null) {
            throw new QuizException(
                "Session Expired",
                "Your quiz session has expired or is invalid.",
                "Please start a new quiz from the home page."
            );
        }

        // Validate question index is within bounds
        if (currentIndex < 0) {
            throw new QuizException(
                "Invalid Question Index",
                "The question index cannot be negative.",
                "Current index: " + currentIndex
            );
        }

        if (currentIndex >= quiz.getQuestions().size()) {  // if you complete all questions, we go to the submit page
            throw new QuizException(
                "Quiz Completed",
                "You have already answered all questions.",
                "Please submit your quiz to see the results."
            );
        }

        Question currentQuestion = quiz.getQuestions().get(currentIndex);   // we get the current question and pass to the view
        model.addAttribute("quiz", quiz);
        model.addAttribute("question", currentQuestion);
        model.addAttribute("currentIndex", currentIndex);
        model.addAttribute("totalQuestions", quiz.getQuestions().size());

        return "quiz";  // returns html view temp we created
    }


    /**
     * Saves the user's answer for a specific question and advances to the next question.
     * If the user is on the last question, the index is not incremented, allowing them to review or resubmit their answer
     */
    @PostMapping("/answer")
    public String saveAnswer(@RequestParam int questionId,
                             @RequestParam String answer,
                             HttpSession session) {
        // Validate session state
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
        Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");

        if (quiz == null || currentIndex == null || userAnswers == null) {
            throw new QuizException(
                "Session Expired",
                "Your quiz session has expired.",
                "Please start a new quiz from the home page."
            );
        }

        // Validate answer is not empty
        if (answer == null || answer.trim().isEmpty()) {
            throw new QuizException(
                "Invalid Answer",
                "Please select an answer before proceeding.",
                "Answer cannot be empty."
            );
        }

        // Validate question ID is valid
        if (questionId <= 0) {
            throw new QuizException(
                "Invalid Question",
                "The question ID is invalid.",
                "Question ID: " + questionId
            );
        }

        // Validate question exists in current quiz
        boolean questionExists = quiz.getQuestions().stream()
            .anyMatch(q -> q.getId() == questionId);
        if (!questionExists) {
            throw new QuizException(
                "Question Not Found",
                "The specified question does not exist in this quiz.",
                "Question ID: " + questionId
            );
        }

        // map of our answers from current session and store the new one
        userAnswers.put(questionId, answer);
        session.setAttribute("userAnswers", userAnswers);

        // moves to next question if we are not at the last one '10'
        // Check if there are more questions remaining
        // Only increment index if not on the last question (size - 1)
        // This allows users to stay on the last question to review/resubmit
        if (currentIndex < quiz.getQuestions().size() - 1) {
            session.setAttribute("currentQuestionIndex", currentIndex + 1);
        }

        return "redirect:/quiz/question";
    }

    /**
     * Navigates back to the previous question in the quiz.
     * <p>
     * This method allows users to review and potentially change their answers
     * to previous questions. It decrements the question index only if the user
     * is not already on the first question.
     *
     */
    @PostMapping("/previous")
    public String previousQuestion(HttpSession session) {  // goes back to previous quetion unless we are at the first one
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");

        // Validate session state
        if (quiz == null || currentIndex == null) {
            throw new QuizException(
                "Session Expired",
                "Your quiz session has expired.",
                "Please start a new quiz from the home page."
            );
        }

        // Validate we're not at the first question
        if (currentIndex <= 0) {
            throw new QuizException(
                "Cannot Go Back",
                "You are already at the first question.",
                "Current question index: " + currentIndex
            );
        }

        // Only decrement if not at the first question (index > 0)
        // This prevents negative index values and navigation errors
        if (currentIndex > 0) {
            session.setAttribute("currentQuestionIndex", currentIndex - 1);
        }
        return "redirect:/quiz/question";
    }

    /**
     * Submits the quiz for evaluation and displays the results.
     * This method handles the final submission of the quiz and performs these operations:
     * Optionally saves the last answer if provided
     * Retrieves quiz and all user answers from session
     * Validates session state
     * Evaluates the quiz using the QuizService
     * Adds results to the model
     * Cleans up session attributes
     * Displays the results page
     */
    @PostMapping("/submit")
    public String submitQuiz(@RequestParam(required = false) Integer questionId,
                             @RequestParam(required = false) String answer,
                             HttpSession session,
                             Model model) {
        // gets us the quiz aqnd answers from the session
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");
        Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");

        // Validation: Check for session expiration or invalid state
        if (quiz == null || userAnswers == null) {
            throw new QuizException(
                "Session Expired",
                "Your quiz session has expired.",
                "Please start a new quiz from the home page."
            );
        }

        //saves the last answered questin
        if (questionId != null && answer != null) {
            // Validate question ID is valid
            if (questionId <= 0) {
                throw new QuizException(
                    "Invalid Question",
                    "The question ID is invalid.",
                    "Question ID: " + questionId
                );
            }

            // Validate answer is not empty
            if (answer.trim().isEmpty()) {
                throw new QuizException(
                    "Invalid Answer",
                    "Please select an answer before submitting.",
                    "Answer cannot be empty."
                );
            }

            userAnswers.put(questionId, answer);
            session.setAttribute("userAnswers", userAnswers);
        }

        // Validate user has answered at least one question
        if (userAnswers.isEmpty()) {
            throw new QuizException(
                "No Answers Submitted",
                "You must answer at least one question before submitting.",
                "Please answer the quiz questions."
            );
        }

        // Evaluate the quiz by comparing user answers with correct answers
        // The QuizService calculates score, percentage, and other metrics
        QuizResult result = quizService.evaluateQuiz(quiz, userAnswers);
        model.addAttribute("result", result);  // we pass result to our html file result
        // clears the sess after finishing
        session.removeAttribute("currentQuiz");
        session.removeAttribute("currentQuestionIndex");
        session.removeAttribute("userAnswers");

        return "result";
    }

    /**
     * Allows a user to retake a quiz by invalidating the current session and starting fresh
     * <p>
     * This method provides a clean way to refresh a quiz, ensuring no previous attempt data persist.
     * Useful when completing a quiz or when the user wants to start over completely
     */
    @GetMapping("/retake/{quizId}")  // resets all the quiz data and redirect us to question 1
    public String retakeQuiz(@PathVariable int quizId, HttpSession session) {
        // Validate quiz ID is positive
        if (quizId <= 0) {
            throw new QuizException(
                "Invalid Quiz ID",
                "The quiz ID must be a positive number.",
                "Received quiz ID: " + quizId
            );
        }

        // Invalidate the entire session to ensure a completely clean state
        // This removes all session attributes, not just quiz-related ones
        session.invalidate();

        // Redirect to the start endpoint to begin a new quiz attempt
        // A new session will be automatically created

        return "redirect:/quiz/start/" + quizId;
    }
}