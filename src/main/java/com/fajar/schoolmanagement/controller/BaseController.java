package com.fajar.schoolmanagement.controller;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fajar.schoolmanagement.entity.Page;
import com.fajar.schoolmanagement.entity.Profile;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.service.ComponentService;
import com.fajar.schoolmanagement.service.RegistryService;
import com.fajar.schoolmanagement.service.UserAccountService;
import com.fajar.schoolmanagement.service.UserSessionService;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;
import com.fajar.schoolmanagement.util.MvcUtil; 
@Controller 
public class BaseController {
	
	protected String basePage;
	
	@Autowired
	protected WebConfigService webAppConfiguration;
	@Autowired
	protected UserSessionService userSessionService;
	@Autowired
	protected UserAccountService accountService;
	@Autowired
	protected RegistryService registryService; 
	@Autowired
	protected UserSessionService userService; 
	@Autowired
	protected ComponentService componentService; 
 


	@ModelAttribute("shopProfile")
	public Profile getProfile(HttpServletRequest request) {
//		System.out.println("Has Session: "+userSessionService.hasSession(request, false));
		return webAppConfiguration.getProfile();
	}
	
	@ModelAttribute("timeGreeting")
	public String timeGreeting(HttpServletRequest request) {
		return DateUtil.getTimeGreeting();
	}
	
	@ModelAttribute("loggedUser")
	public User getLoggedUser(HttpServletRequest request) {
		if(userSessionService.hasSession(request, false)) {
			return userSessionService.getUserFromSession(request);
		}
		else return null;
	} 
	
	@ModelAttribute("host")
	public String getHost(HttpServletRequest request) {
		return MvcUtil.getHost(request);
	}
	
	@ModelAttribute("contextPath")
	public String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}
	
	@ModelAttribute("fullImagePath")
	public String getFullImagePath(HttpServletRequest request) {
		return getHost(request)+ getContextPath(request)+"/"+getUploadedImagePath(request)+"/";
	}
	
	@ModelAttribute("imagePath")
	public String getUploadedImagePath(HttpServletRequest request) {
		return webAppConfiguration.getUploadedImagePath();
	}
	
	@ModelAttribute("pageToken")
	public String pageToken(HttpServletRequest request) {
		  return accountService.getToken(request);
	}
	
	@ModelAttribute("requestId")
	public String requestId(HttpServletRequest request) {
		Cookie cookie = getCookie(RegistryService.JSESSSIONID, request.getCookies());
		String cookieValue = cookie == null ? UUID.randomUUID().toString():cookie.getValue();
		return	registryService.addPageRequest(  cookieValue);
		 
	}
	
	@ModelAttribute("pages")
	public List<Page> pages(HttpServletRequest request){
		
		return componentService.getPages(request);
	}
	 
	public String activePage(HttpServletRequest request) {
		return userSessionService.getPageCode(request);
	}
	
	public void setActivePage(HttpServletRequest request ) {
		
		String pageCode = componentService.getPageCode(request);
		userSessionService.setActivePage(request, pageCode);
	}
	
	/**
	 * ======================================================
	 * 				     	Statics
	 * ======================================================
	 * 
	 */
	
	public static Cookie getCookie(String name, Cookie[] cookies) {
		try {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals(name)) { return cookie; }
			}
		}catch(Exception ex) { ex.printStackTrace(); }
		return null;
	}
	
	/**
	 * send to login page URL
	 * @param request
	 * @param response
	 */
	public static void sendRedirectLogin(HttpServletRequest request, HttpServletResponse response) {
		sendRedirect(response, request.getContextPath() + "/account/login");
	}
	
	/**
	 * send to specified URL
	 * @param response
	 * @param url
	 */
	public static void sendRedirect(HttpServletResponse response ,String url)  {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

