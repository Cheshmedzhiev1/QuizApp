package com.quiz.controller;

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

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizLoader quizLoader;

    @Autowired
    private QuizService quizService;

    @GetMapping("/start/{quizId}")
    public String startQuiz(@PathVariable int quizId, HttpSession session) {
        Quiz quiz = quizLoader.getQuizById(quizId);  //retrieves us the quiz by the id
        if (quiz == null) {    // if quiz does not exist, redirects us to the main page
            return "redirect:/";
        }

        Quiz shuffledQuiz = quizService.shuffleQuiz(quiz);   // we shuffle the question to any random order

        session.setAttribute("currentQuiz", shuffledQuiz);
        session.setAttribute("currentQuestionIndex", 0);
        session.setAttribute("userAnswers", new HashMap<Integer, String>());

        return "redirect:/quiz/question";  //brings us back to the first question
    }

    @GetMapping("/question")
    public String showQuestion(HttpSession session, Model model) {
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");  // retrieves quiz and current question index from current session
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");

        if (quiz == null || currentIndex == null) {
            return "redirect:/";  // if there is no quiz, back to home page
        }

        if (currentIndex >= quiz.getQuestions().size()) {  // if you complete all questions, we go to the submit page
            return "redirect:/quiz/submit";
        }

        Question currentQuestion = quiz.getQuestions().get(currentIndex);   // we get the current question and pass to the view
        model.addAttribute("quiz", quiz);
        model.addAttribute("question", currentQuestion);
        model.addAttribute("currentIndex", currentIndex);
        model.addAttribute("totalQuestions", quiz.getQuestions().size());

        return "quiz";  // returns html view temp we created
    }

    @PostMapping("/answer")
    public String saveAnswer(@RequestParam int questionId,
                             @RequestParam String answer,
                             HttpSession session) {
      // map of our answers from current session and store the new one
        Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");
        userAnswers.put(questionId, answer);
        session.setAttribute("userAnswers", userAnswers);

   // moves to next question if we are not at the last one '10'
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");


        if (currentIndex < quiz.getQuestions().size() - 1) {
            session.setAttribute("currentQuestionIndex", currentIndex + 1);
        }

        return "redirect:/quiz/question";
    }

    @PostMapping("/previous")
    public String previousQuestion(HttpSession session) {  // goes back to previous quetion unless we are at the first one
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
        if (currentIndex > 0) {
            session.setAttribute("currentQuestionIndex", currentIndex - 1);
        }
        return "redirect:/quiz/question";
    }

    @PostMapping("/submit")
    public String submitQuiz(@RequestParam(required = false) Integer questionId,
                             @RequestParam(required = false) String answer,
                             HttpSession session,
                             Model model) {
  //saves the last answered questin
        if (questionId != null && answer != null) {
            Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");
            userAnswers.put(questionId, answer);
            session.setAttribute("userAnswers", userAnswers);
        }
// gets us the quiz aqnd answers from the session
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");
        Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");

        if (quiz == null || userAnswers == null) {  //if session is expired or invalid, back to home page
            return "redirect:/";
        }

        QuizResult result = quizService.evaluateQuiz(quiz, userAnswers);
        model.addAttribute("result", result);  // we pass result to our html file result
// clears the sess after finishing
        session.removeAttribute("currentQuiz");
        session.removeAttribute("currentQuestionIndex");
        session.removeAttribute("userAnswers");

        return "result";
    }

    @GetMapping("/retake/{quizId}")  // resets all the quiz data and redirect us to question 1
    public String retakeQuiz(@PathVariable int quizId, HttpSession session) {
        session.invalidate();
        return "redirect:/quiz/start/" + quizId;
    }
}