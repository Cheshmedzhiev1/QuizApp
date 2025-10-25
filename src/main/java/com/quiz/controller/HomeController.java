package com.quiz.controller;

import com.quiz.service.QuizLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private QuizLoader quizLoader;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("quizzes", quizLoader.getAllQuizzes());
        return "home";
    }
}