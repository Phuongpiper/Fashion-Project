package com.poly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.entity.User;
import com.poly.repository.UserRepository;
import com.poly.service.SessionService;


import jakarta.websocket.Session;

@Controller
public class UpdatePasswordController {
	@Autowired
	UserRepository dao;

	@Autowired
	SessionService sessionService;

	@GetMapping("/account/xacNhanMK")
	public String xacNhanMK(Model model) {
		String Message = sessionService.get("Message");
	    if (Message != null) {
	        model.addAttribute("Message", Message);
	        sessionService.remove("Message");
	    }
		return "xacNhanMK";
	}
	@PostMapping("/account/xacNhanMK")
	public String xacNhanMK(Model model, @ModelAttribute("user") User user, @RequestParam("MXN") String mxn, @RequestParam("nhapLai") String nhapLai) {
		String email = sessionService.get("Email");
		String MXN = sessionService.get("MXN");
		if (nhapLai.equals(user.getPassword())) {
			if (MXN.equals(mxn)) {
				User customer = dao.findByEmail(email);
				customer.setPassword(user.getPassword());
				dao.save(customer);
				sessionService.remove("MXN");
				sessionService.remove("Email");
			}else {
				sessionService.set("Message", "Mã xác nhận sai!!!");
				return "redirect:/account/xacNhanMK";
			}
		}else {
			sessionService.set("Message", "Mật khẩu không trùng khớp!!!");
			return "redirect:/account/xacNhanMK";
		}
		sessionService.set("Message", "Đổi mật khẩu thành công!!!");
		model.addAttribute("successMessage", "Cập nhật tài khoản thành công!");

		return "login";
	}
}
