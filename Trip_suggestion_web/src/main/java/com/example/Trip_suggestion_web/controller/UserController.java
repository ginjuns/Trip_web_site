package com.example.Trip_suggestion_web.controller;

import com.example.Trip_suggestion_web.entity.User;
import com.example.Trip_suggestion_web.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(
            UserRepository userRepository
    ) {

        this.userRepository =
                userRepository;

    }

    @GetMapping("/signup")
    public String signupForm() {

        return "signup";

    }
    @PostMapping("/signup")
    public String signup(

            String name,

            String username,

            String password,

            Model model

    ) {

        User existingUser =
                userRepository.findByUsername(
                        username
                );
        if (existingUser != null) {
            model.addAttribute(
                    "error",
                    "이미 사용 중인 아이디입니다."
            );
            return "signup";
        }
        User user =
                new User(
                        username,
                        password,
                        name
                );
        userRepository.save(
                user
        );
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
    @PostMapping("/login")
    public String login(

            String username,

            String password,

            HttpSession session,

            Model model

    ) {

        User user =
                userRepository.findByUsername(
                        username
                );

        if (user == null) {

            model.addAttribute(
                    "error",
                    "존재하지 않는 아이디입니다."
            );

            return "login";

        }

        if (!user.getPassword().equals(
                password
        )) {

            model.addAttribute(
                    "error",
                    "비밀번호가 틀렸습니다."
            );

            return "login";

        }

        session.setAttribute(
                "loginUser",
                user
        );

        return "redirect:/";

    }
    @GetMapping("/logout")
    public String logout(

            HttpSession session

    ) {

        session.invalidate();

        return "redirect:/";

    }
}