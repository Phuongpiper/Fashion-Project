package com.poly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.entity.User;
import com.poly.repository.UserRepository;
import com.poly.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired
	UserRepository dao;

	@Autowired
	SessionService session;

	@Autowired
	HttpServletRequest request;

	@GetMapping("/capnhatUser")
	public String usermanagement(Model model) {
		Integer userId = (Integer) request.getSession().getAttribute("userId");
		if (userId != null) {
			User user = dao.findById(userId).orElse(null);
			if (user != null) {
				model.addAttribute("user", user);
				return "capnhatUser";
			}
		}
		return "index";
	}

	@PostMapping("/capnhatUser/update")
	public String updateUser(@Valid  User user, BindingResult result, Model model,
			HttpServletRequest request) {
        System.err.println(user);
		Integer userId = (Integer) request.getSession().getAttribute("userId");

		User user1 = dao.findById(userId).orElse(null);
		System.err.println(user.getUserId());
		System.err.println(user.getEmail());
		user1.setUsername(user.getUsername());
		user1.setPassword(user.getPassword());
		user1.setEmail(user.getEmail());

		if (result.hasErrors()) {
			model.addAttribute("messages", "Invalid user data");
			System.err.println(result.toString());
			return "capnhatUser";
		}
		// Lưu người dùng đã cập nhật vào cơ sở dữ liệu
		dao.save(user1);
        model.addAttribute("messages", "Cập nhật thành công");
		return "/capnhatUser";
	}

}
