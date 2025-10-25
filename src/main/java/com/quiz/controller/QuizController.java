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
        Quiz quiz = quizLoader.getQuizById(quizId);
        if (quiz == null) {
            return "redirect:/";
        }

        Quiz shuffledQuiz = quizService.shuffleQuiz(quiz);
        session.setAttribute("currentQuiz", shuffledQuiz);
        session.setAttribute("currentQuestionIndex", 0);
        session.setAttribute("userAnswers", new HashMap<Integer, String>());

        return "redirect:/quiz/question";
    }

    @GetMapping("/question")
    public String showQuestion(HttpSession session, Model model) {
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");

        if (quiz == null || currentIndex == null) {
            return "redirect:/";
        }

        if (currentIndex >= quiz.getQuestions().size()) {
            return "redirect:/quiz/submit";
        }

        Question currentQuestion = quiz.getQuestions().get(currentIndex);
        model.addAttribute("quiz", quiz);
        model.addAttribute("question", currentQuestion);
        model.addAttribute("currentIndex", currentIndex);
        model.addAttribute("totalQuestions", quiz.getQuestions().size());

        return "quiz";
    }

    @PostMapping("/answer")
    public String saveAnswer(@RequestParam int questionId,
                             @RequestParam String answer,
                             HttpSession session) {

        Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");
        userAnswers.put(questionId, answer);
        session.setAttribute("userAnswers", userAnswers);


        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");


        if (currentIndex < quiz.getQuestions().size() - 1) {
            session.setAttribute("currentQuestionIndex", currentIndex + 1);
        }

        return "redirect:/quiz/question";
    }

    @PostMapping("/previous")
    public String previousQuestion(HttpSession session) {
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

        if (questionId != null && answer != null) {
            Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");
            userAnswers.put(questionId, answer);
            session.setAttribute("userAnswers", userAnswers);
        }

        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");
        Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");

        if (quiz == null || userAnswers == null) {
            return "redirect:/";
        }

        QuizResult result = quizService.evaluateQuiz(quiz, userAnswers);
        model.addAttribute("result", result);

        session.removeAttribute("currentQuiz");
        session.removeAttribute("currentQuestionIndex");
        session.removeAttribute("userAnswers");

        return "result";
    }

    @GetMapping("/retake/{quizId}")
    public String retakeQuiz(@PathVariable int quizId, HttpSession session) {
        session.invalidate();
        return "redirect:/quiz/start/" + quizId;
    }
}