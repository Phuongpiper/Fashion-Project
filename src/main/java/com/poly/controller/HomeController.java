package com.poly.controller;

import java.util.Date;
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

import com.poly.entity.Cart;
import com.poly.entity.CartProduct;
import com.poly.entity.Category;
import com.poly.entity.Product;
import com.poly.entity.User;

import com.poly.repository.CartRepository;
import com.poly.repository.CategoryRepository;
import com.poly.repository.ProductRepository;
import com.poly.repository.UserRepository;
import com.poly.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	UserRepository dao;

	@Autowired
	ProductRepository dao1;

	@Autowired
	CategoryRepository dao2;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	HttpServletRequest request;

	@Autowired
	SessionService session;

	@GetMapping("/index")
	public String index3(Model model, Product p) {
		List<Product> page = dao1.findFirst10ByOrderByDiscountDesc();
		model.addAttribute("products", page);
		return "index";
	}

	@GetMapping("/contact")
	public String contact(Model model) {
		return "contact";
	}

	@GetMapping("/login")
	public String showLoginForm(Model model) {
		model.addAttribute("user", new User());
		return "login";
	}

	@PostMapping("/login")
	public String processLoginForm(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
			@RequestParam("username") String username, @RequestParam("password") String password,
			HttpServletRequest request, HttpServletResponse response) {

	    
		user = dao.findByUsername(username);

		if (user != null && user.getPassword().equals(password)) {
			if (user.isIsative()== false) {
				model.addAttribute("messages", "Tài khoản của bạn đã bị vô hiệu hóa.");
				return "login";
			} else {
				if (user.isRole()) {
					session.set("userSession", user);
					return "redirect:/usermanagement";
				} else {
					// Kiểm tra xem người dùng đã có giỏ hàng trước đó hay chưa
					Cart cart = null;
					session.set("userSession", user);
					if (user.getCarts().isEmpty()) {
						// Nếu chưa có giỏ hàng, tạo giỏ hàng mới
						cart = new Cart();
						cart.setUser(user);
						cart.setCartDate(new Date());
						// Lưu đối tượng Cart vào cơ sở dữ liệu bằng JpaRepository
						cartRepository.save(cart);
					} else {
						// Nếu đã có giỏ hàng, sử dụng lại giỏ hàng đó
						cart = user.getCarts().get(0);
					}

					// Lưu cartId vào session
					request.getSession().setAttribute("cartId", cart.getCartId());
					request.getSession().setAttribute("user", user.getUsername());
					request.getSession().setAttribute("userId", user.getUserId());

					return "redirect:/index";
				}
			}
		}
		model.addAttribute("messages", "Đăng nhập thất bại");
		if (result.hasErrors()) {
			return "login";
		}
		return "login";
	}

	// Các phương thức xử lý khác trong controller

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		// Xóa các thuộc tính trong session
		session.invalidate();

		return "redirect:/login"; // Chuyển hướng người dùng đến trang đăng nhập sau khi logout
	}

}
