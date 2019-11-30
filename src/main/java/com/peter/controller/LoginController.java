package com.peter.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("login")
public class LoginController {

	@PostMapping("user")
	public String login(@RequestParam("username") String username, @RequestParam("password") String password,
			Map<String, Object> map, HttpSession session) {
		if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password) 
				&& "123456".equals(password)
				&& "admin".equals(username)) {
			session.setAttribute("username", username);
			return "redirect:/main.html";
		} else {
			map.put("msg", "用户名或者密码错误");
			return "login";
		}
	}
}
