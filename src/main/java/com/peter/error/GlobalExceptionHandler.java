package com.peter.error;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

	//
	@ExceptionHandler(Exception.class)
	
	public String handlerException(Exception exception,HttpServletRequest request) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("message", exception.getMessage());
		request.setAttribute("javax.servlet.error.status_code", 400);
		request.setAttribute("ext", map);
		//接用springboot自身的客户端自适应
		return "forward:/error";
	}
}
