package com.quiz.controller;

import com.quiz.service.QuizLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class responsible for handling home page requests and displaying available quizzes.
 * <p>
 * This controller serves as the entry point to the quiz application, presenting users
 * with a list of all available quizzes that they can select to begin taking.
 */
@Controller
public class HomeController {


    // Service component responsible for loading quiz data from the data source
    @Autowired
    private QuizLoader quizLoader;

    /**
     * Handles HTTP GET requests to the application root and displays the home page.
     *
     * <p>This method serves as the landing page for the quiz application. It retrieves
     * all available quizzes from the data source using the {@link QuizLoader} service
     * and makes them available to the view through the Spring MVC {@link Model}.</p>
     *
     * <p><strong>Request Processing Flow:</strong></p>
     * <ol>
     *   <li>User navigates to the root URL ("/") of the application</li>
     *   <li>Spring MVC routes the request to this method</li>
     *   <li>Method calls QuizLoader to retrieve all available quizzes</li>
     *   <li>Quiz data is added to the Model with attribute name "quizzes"</li>
     *   <li>Method returns view name "home" for rendering</li>
     *   <li>ViewResolver resolves "home" to the appropriate template (e.g., home.html)</li>
     *   <li>View renders with the quiz data available</li>
     * </ol>
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("quizzes", quizLoader.getAllQuizzes());

        return "home";
    }
}