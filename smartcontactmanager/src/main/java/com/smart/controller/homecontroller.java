package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.rep.UserRepo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller 
public class homecontroller {
    
    @Autowired
    private UserRepo userrepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Inject the PasswordEncoder bean
    
    @RequestMapping("/")
    public String home(Model m) {
        m.addAttribute("title", "Smart Contact");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model m) {
        m.addAttribute("title", "About Smart Contact");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model m) {
        m.addAttribute("title", "Register - Smart Contact");
        m.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping("/login")
    public String login(Model m) {
        m.addAttribute("title", "Login - Smart Contact");
        return "login";
    }
    @GetMapping("/logout")
    public String logout() {
        // Perform logout logic here, such as clearing session or tokens
        return "redirect:/login?logout"; // Redirect to login page with logout parameter
    }
    @PostMapping("/do_register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result1, 
                           @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, 
                           Model m, HttpSession session) {
        
        try {
            if (result1.hasErrors()) {
                System.out.println(result1.toString());
                m.addAttribute("user", user);
                return "signup";
            }
            System.out.println(agreement);
            System.out.println(user);

            user.setRole("ROLE_USER");
            user.setEnabled(true);

            // Encode the password before saving the user
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User result = this.userrepo.save(user);
            m.addAttribute("user", new User());

            session.setAttribute("message", new Message("Register successfully", "success"));
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            m.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "danger"));
            return "signup";
        }
    }
}
