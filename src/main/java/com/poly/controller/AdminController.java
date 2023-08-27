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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.entity.User;
import com.poly.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
public class AdminController {
	@Autowired
	UserRepository RepoUser;

	@GetMapping("/usermanagement")
	public String usermanagement(Model model, @ModelAttribute("user") User user, @RequestParam(value = "page", defaultValue = "0") int page) {
	    int pageSize = 5; // Số mục trên mỗi trang
	    Pageable pageable = PageRequest.of(page, pageSize);

	    Page<User> userPage = RepoUser.findAll(pageable);
	    List<User> users = userPage.getContent();
	 // Kiểm tra nếu trang hiện tại lớn hơn tổng số trang, đặt trang hiện tại về giá trị cuối cùng của tổng số trang
	    int totalPages = userPage.getTotalPages();
	    if (page >= totalPages) {
	        page = totalPages - 1;
	    }
	    model.addAttribute("users", users);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", userPage.getTotalPages());

	    return "/admin/usermanagement";
	}


	@GetMapping("/usermanagement/edit/")
	public String usermanagement_edit(Model model, @RequestParam("userId") int userId, User user, @RequestParam(value = "page", defaultValue = "0") int page) {
	    int pageSize = 5; // Số mục trên mỗi trang
	    Pageable pageable = PageRequest.of(page, pageSize);

	    user = RepoUser.findById(userId).orElse(null);
	    model.addAttribute("user", user);

	    Page<User> userPage = RepoUser.findAll(pageable);
	    List<User> users = userPage.getContent();

	    model.addAttribute("users", users);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", userPage.getTotalPages());

	    return "/admin/usermanagement";
	}


	@PostMapping("/usermanagement/create")
	public String usermanagement_create(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
	    int pageSize = 5; // Số mục trên mỗi trang
	    Pageable pageable = PageRequest.of(page, pageSize);

	   // Kiểm tra trùng tên đăng nhập
	    if (RepoUser.existsByUsername(user.getUsername())) {
	        result.rejectValue("username", "duplicate", "Username already exists");
	    }

	    // Kiểm tra trùng email
	    if (RepoUser.existsByEmail(user.getEmail())) {
	        result.rejectValue("email", "duplicate", "Email already exists");
	    }

	    if (result.hasErrors()) {
	        Page<User> userPage = RepoUser.findAll(pageable);
	        List<User> users = userPage.getContent();

	        model.addAttribute("users", users);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", userPage.getTotalPages());

	        return "/admin/usermanagement"; // Trả
	    } else {
	        user.setIsative(true);
	        RepoUser.save(user);
	        model.addAttribute("message", "Create success");

	        Page<User> userPage = RepoUser.findAll(pageable);
	        List<User> users = userPage.getContent();

	        model.addAttribute("users", users);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", userPage.getTotalPages());

	        return "/admin/usermanagement";
	    }
	}


	@PostMapping("/usermanagement/update")
	public String usermanagement_update(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
	        @RequestParam(value = "page", defaultValue = "0") int page) {
	    int pageSize = 5; // Số mục trên mỗi trang
	    Pageable pageable = PageRequest.of(page, pageSize);

	    if (result.hasErrors()) {
	        Page<User> userPage = RepoUser.findAll(pageable);
	        List<User> users = userPage.getContent();

	        model.addAttribute("users", users);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", userPage.getTotalPages());

	        return "/admin/usermanagement"; // Trả về trang usermanagement nếu có lỗi
	    } else {
	        Optional<User> existingUser = RepoUser.findById(user.getUserId());
	        if (existingUser.isPresent()) {
	            User updatedUser = existingUser.get();
	            updatedUser.setIsative(true);
	            updatedUser.setUsername(user.getUsername());
	            updatedUser.setEmail(user.getEmail());
	            // Cập nhật các thuộc tính khác tại đây

	            RepoUser.save(updatedUser);
	            model.addAttribute("message", "Update success");
	        } else {
	            model.addAttribute("error", "User not found");
	        }

	        Page<User> userPage = RepoUser.findAll(pageable);
	        List<User> users = userPage.getContent();

	        model.addAttribute("users", users);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", userPage.getTotalPages());

	        return "/admin/usermanagement";
	    }
	}


	@PostMapping("/usermanagement/delete")
	public String usermanagement_delete(Model model, @RequestParam("userId") int userId, User user, @RequestParam(value = "page", defaultValue = "0") int page) {
	    int pageSize = 5; // Số mục trên mỗi trang
	    Pageable pageable = PageRequest.of(page, pageSize);

	    if (RepoUser.findById(userId).isEmpty()) {
	        model.addAttribute("error", "Not UserId");
	    } else {
	        user.setIsative(false);
	        RepoUser.save(user);
	        model.addAttribute("message", "Delete success");
	    }

	    Page<User> userPage = RepoUser.findAll(pageable);
	    List<User> users = userPage.getContent();

	    int totalPages = userPage.getTotalPages();

	    if (page >= totalPages) {
	        // Giảm trang hiện tại xuống 1 nếu nó lớn hơn hoặc bằng tổng số trang
	        page = Math.max(totalPages - 1, 0);
	    }
	    model.addAttribute("users", users);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", userPage.getTotalPages());

	    return "/admin/usermanagement";
	}


	@PostMapping("/usermanagement/reset")
	public String usermanagement_reset(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
	    int pageSize = 5; // Số mục trên mỗi trang
	    Pageable pageable = PageRequest.of(page, pageSize);

	    User user = new User();
	    model.addAttribute("user", user);

	    Page<User> userPage = RepoUser.findAll(pageable);
	    List<User> users = userPage.getContent();

	    model.addAttribute("users", users);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", userPage.getTotalPages());

	    return "/admin/usermanagement";
	}

}
