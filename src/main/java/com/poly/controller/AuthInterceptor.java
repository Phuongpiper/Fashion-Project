package com.poly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.poly.entity.User;
import com.poly.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Service
public class AuthInterceptor implements HandlerInterceptor {
	@Autowired
	SessionService session;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		User user = session.get("userSession"); // lấy từ session
		
		if (user == null) {
			response.sendRedirect("/login");
			return false;
		} else if (user.isRole() == false) {
			response.sendRedirect("/login");
			return false;
		} else if (user.isRole()) {
			return true;
		}
		return true;
	}
	
	
}
