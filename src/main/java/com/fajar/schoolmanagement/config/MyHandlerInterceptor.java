package com.fajar.schoolmanagement.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.fajar.schoolmanagement.annotation.Authenticated;
import com.fajar.schoolmanagement.controller.BaseController;
import com.fajar.schoolmanagement.service.UserSessionService;

import lombok.extern.slf4j.Slf4j;


/**
 * this class is registered in the xml configuration
 * @author Republic Of Gamers
 *
 */
@Slf4j
public class MyHandlerInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private UserSessionService userSessionService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		 
		log.info("[preHandle][" + request + "]" + "[" + request.getMethod()  + "]" );
		
		log.info("[handler class] {}", handler.getClass());
		return super.preHandle(request, response, handler);
	}
	
	 
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception { 
		
		/////CHECK IF WEB PAGE/////
		if(modelAndView != null && handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler; 
			boolean authenticationRequired = handlerMethod.getMethod().getAnnotation(Authenticated.class) != null;
			
			log.info("URI: {} requires authentication: {}", request.getRequestURI(), authenticationRequired);
			
			if(authenticationRequired) {
				if(!userSessionService.hasSession(request)) {
					log.info("URI: {} not authenticated, will redirect to login page", request.getRequestURI());
					BaseController.sendRedirectLogin(request, response);
				}
			}
			
			log.info("[handler Class] {}", handler.getClass());
			log.info("[postHandle], viewName: {}", modelAndView.getViewName());
		}
		
		super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		super.afterCompletion(request, response, handler, ex);
	}
	

}
