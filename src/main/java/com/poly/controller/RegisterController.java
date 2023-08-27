package com.poly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.poly.entity.User;
import com.poly.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class RegisterController {
	@Autowired
	UserRepository RepoUser;

	@GetMapping("/register")
	public String register(Model model, @ModelAttribute("user") User user) {
		model.addAttribute("users", RepoUser.findAll()); // Truyền danh sách dữ liệu vào model
		return "/register"; 
	}

	@PostMapping("/register")
	public String registerPost(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
	    if (result.hasErrors()) {
	        return "/register";
	    } else {
	        // Kiểm tra trùng tên đăng nhập
	        User existingUser = RepoUser.findByUsername(user.getUsername());
	        if (existingUser != null) {
	            result.rejectValue("username", "error.user", "Username is already taken");
	            return "/register";
	        }
	        
	        // Kiểm tra trùng email
	        existingUser = RepoUser.findByEmail(user.getEmail());
	        if (existingUser != null) {
	            result.rejectValue("email", "error.user", "Email is already registered");
	            return "/register";
	        }
	        
	        user.setIsative(true);
	        RepoUser.save(user);
	        model.addAttribute("messages", "Register success. ");
	        model.addAttribute("users", RepoUser.findAll());
	        return "/register";
	    }
	}

}
