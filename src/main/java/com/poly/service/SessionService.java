package com.poly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
@Service
public class SessionService {
	@Autowired
	HttpSession session;
	/**
	* Đọc giá trị của attribute trong session
	* @param name tên attribute
	* @return giá trị đọc được hoặc null nếu không tồn tại
	*/
	public <T> T get(String name) {
		return (T) session.getAttribute(name);
	};
	/**
	* Thay đổi hoặc tạo mới attribute trong session
	* @param name tên attribute
	* @param value giá trị attribute
	*/
	public void set(String name, Object value) {
		session.setAttribute(name, value);
	};
	/**
	* Xóa attribute trong session
	* @param name tên attribute cần xóa
	*/
	public void remove(String name) {
		 session.removeAttribute(name);
	};
	

    private final HttpServletRequest request;

    public SessionService(HttpServletRequest request) {
        this.request = request;
    }

    public Object get(String key, Object defaultValue) {
        HttpSession session = request.getSession();
        Object value = session.getAttribute(key);
        return value != null ? value : defaultValue;
    }
}
