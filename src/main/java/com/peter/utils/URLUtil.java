package com.peter.utils;


import javax.servlet.http.HttpServletRequest;

public class URLUtil {
	public static String getUrl(HttpServletRequest request) {
		String contextPath=request.getContextPath();
		String servletPath = request.getServletPath();
		String queryString = request.getQueryString();
		if (queryString==null) {
			queryString="";
		}
		if (queryString!=null&&queryString.contains("&pc=")) {
			queryString=queryString.substring(0, queryString.lastIndexOf("&pc="));
		}
		return queryString!=null?contextPath+servletPath+"?"+queryString:contextPath+servletPath;
	}
}