package com.fajar.schoolmanagement.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.fajar.schoolmanagement.dto.KeyValue;
import com.fajar.schoolmanagement.entity.Page;
import com.fajar.schoolmanagement.entity.Profile;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.service.ComponentService;
import com.fajar.schoolmanagement.service.RuntimeService;
import com.fajar.schoolmanagement.service.UserAccountService;
import com.fajar.schoolmanagement.service.UserSessionService;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;
import com.fajar.schoolmanagement.util.MvcUtil;
import com.fajar.schoolmanagement.util.SessionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j; 
@Controller 
@Slf4j
public class BaseController {
	
	protected String basePage;
	
	@Autowired
	protected WebConfigService webConfigService;
	@Autowired
	protected UserSessionService userSessionService;
	@Autowired
	protected UserAccountService accountService;
	@Autowired
	protected RuntimeService registryService; 
	@Autowired
	protected UserSessionService userService; 
	@Autowired
	protected ComponentService componentService; 
	@Autowired	
	protected ObjectMapper objectMapper; 

	@ModelAttribute("profile")
	public Profile getProfile(HttpServletRequest request) {
//		System.out.println("Has Session: "+userSessionService.hasSession(request, false));
		return webConfigService.getProfile();
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
		return webConfigService.getUploadedImagePath();
	}
	
	@ModelAttribute("currentYear")
	public int currentYear(HttpServletRequest request) {
		return DateUtil.getCalendarItem(new Date(), Calendar.YEAR);
	}
	
	@ModelAttribute("pageToken")
	public String pageToken(HttpServletRequest request) {
		  return userSessionService.getToken(request);
	}
	
	@ModelAttribute("requestId")
	public String requestId(HttpServletRequest request) {
		Cookie cookie = getCookie(SessionUtil.JSESSSIONID, request.getCookies());
		String cookieValue = cookie == null ? UUID.randomUUID().toString():cookie.getValue();
		return	registryService.addPageRequest(  cookieValue);
		 
	}
	
	@ModelAttribute("pages")
	public List<Page> pages(HttpServletRequest request){
		
		return componentService.getPages(request);
	}
	
	protected String writeValueAsString(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "{}";
		}
	}
	
	/**
	 * =============== End Model Attributes ===============
	 * 
	 */
	 
	public String activePage(HttpServletRequest request) {
		return userSessionService.getPageCode(request);
	}
//	
//	public void setActivePage(HttpServletRequest request ) {
//		
//		String pageCode = componentService.getPageCode(request);
//		userSessionService.setActivePage(request, pageCode);
//	}
	
	/**
	 * ======================================================
	 * 				     	Statics
	 * ======================================================
	 * 
	 */
	
	public static Cookie getCookie(String name, Cookie[] cookies) {
		if(null ==cookies) {
			return null;
		}
		try {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals(name)) { return cookie; }
			}
		}catch(Exception ex) { ex.printStackTrace(); }
		return null;
	}
	
	private static void addResourcePaths(ModelAndView modelAndView, String resourceName, String... paths) {
		List<KeyValue> resoucePaths = new ArrayList<>();
		for (int i = 0; i < paths.length; i++) {
			resoucePaths.add(KeyValue.builder().value(paths[i]).build());
			log.info("{}. Add {} to {} , value: {}", i, resourceName, modelAndView.getViewName(), paths[i]);
		}
		setModelAttribute(modelAndView, resourceName, resoucePaths);
	}
	
	private static void setModelAttribute(ModelAndView modelAndView, String attrName, Object attrValue) {
		if(null == attrValue || attrValue.toString().trim().isEmpty()) { return ; }
		modelAndView.getModel().put(attrName, attrValue);
	}

	public static void addStylePaths(ModelAndView modelAndView, String... paths) {
		if(null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalStylePaths", paths);
	}

	public static void addJavaScriptResourcePaths(ModelAndView modelAndView, String... paths) {
		if(null == paths) {
			return;
		}
		addResourcePaths(modelAndView, "additionalScriptPaths", paths);
	}

	public static void addTitle(ModelAndView modelAndView, String title) {
		if(null == title || title.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "title", title);
	}

	public static void addPageUrl(ModelAndView modelAndView, String pageUrl) {
		if(null == pageUrl || pageUrl.isEmpty()) {
			return;
		}
		setModelAttribute(modelAndView, "pageUrl", pageUrl);
		
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
		
		log.info("Will sendRedirect to : {}", url);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public static Cookie getJSessionIDCookie(HttpServletRequest request) {

		return getCookie(SessionUtil.JSESSSIONID, request.getCookies());
	}
	
	public void setActivePage(HttpServletRequest request) {

		String pageCode = componentService.getPageCode(request);
		userSessionService.setActivePage(request, pageCode);
	}
}

