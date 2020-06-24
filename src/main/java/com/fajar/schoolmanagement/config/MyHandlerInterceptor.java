package com.fajar.schoolmanagement.config;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fajar.schoolmanagement.annotation.Authenticated;
import com.fajar.schoolmanagement.controller.BaseController;
import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.service.UserSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * this class is registered in the xml configuration
 * 
 * @author Republic Of Gamers
 *
 */
@Slf4j
public class MyHandlerInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
    private org.springframework.context.ApplicationContext appContext;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		log.info("[preHandle][" + request + "]" + "[" + request.getMethod() + "]"); 
		HandlerMethod handlerMethod = getHandlerMethod(request); 
		 
		if(isAPI(handlerMethod)) {
			return handleAPI(request, response, handlerMethod);
		}
		
		return super.preHandle(request, response, handler);
	} 

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("--------------------------- BEGIN POST HANDLE {} ----------------------", request.getRequestURI());

		if (modelAndView != null && handler instanceof HandlerMethod) {
			handleWebPage(request, response, modelAndView, handler);
		} 

		super.postHandle(request, response, handler, modelAndView);
		log.info("--------------------------- END POST HANDLE {} ----------------------", request.getRequestURI());
	}

	private boolean handleAPI(HttpServletRequest request, HttpServletResponse response, Object handler) {

		log.info("intercept api handler: {}", request.getRequestURI());
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		boolean authenticationRequired = getAuthenticationAnnotation(handlerMethod) != null;
		if (authenticationRequired) {
			if (!hasSession(request)) {
				response.setContentType("application/json");
				try {
					response.getWriter().write(objectMapper.writeValueAsString(WebResponse.failed("NOT AUTHENTICATED")));
				} catch (IOException e) { 
					e.printStackTrace();
				}
				return false;
			}
		}
		return true;
	}

	private Authenticated getAuthenticationAnnotation(HandlerMethod handlerMethod) {
		return handlerMethod.getMethod().getAnnotation(Authenticated.class);
	}
	
	private boolean hasSession(HttpServletRequest request) {
		return userSessionService.hasSession(request);
	}

	private void handleWebPage(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView,
			Object handler) {

		log.info("intercept webpage handler: {}", request.getRequestURI());
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		boolean authenticationRequired = getAuthenticationAnnotation(handlerMethod) != null;

		log.info("URI: {} requires authentication: {}", request.getRequestURI(), authenticationRequired);

		if (authenticationRequired) {
			if (!hasSession(request)) {
				log.info("URI: {} not authenticated, will redirect to login page", request.getRequestURI());
				BaseController.sendRedirectLogin(request, response);
			}
		}

		log.info("[handler Class] {}", handler.getClass());
		log.info("[postHandle], viewName: {}", modelAndView.getViewName());
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		super.afterCompletion(request, response, handler, ex);
	}

	
	private HandlerMethod getHandlerMethod(HttpServletRequest request) {
		 HandlerMethod handlerMethod = null;
		  
         try {
             RequestMappingHandlerMapping req2HandlerMapping = (RequestMappingHandlerMapping) appContext.getBean("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping");
             // Map<RequestMappingInfo, HandlerMethod> handlerMethods = req2HandlerMapping.getHandlerMethods();
             HandlerExecutionChain handlerExeChain = req2HandlerMapping.getHandler(request);
             if (Objects.nonNull(handlerExeChain)) {
                 handlerMethod = (HandlerMethod) handlerExeChain.getHandler();
                 
                 log.info("[handler method] {}", handlerMethod.getClass());
                 return handlerMethod;
             }
         } catch (Exception e) {
             log.warn("Lookup the handler method ERROR", e);
         } finally {
             log.debug("URI = " + request.getRequestURI() + ", handlerMethod = " + handlerMethod);
         }
         
         return null;
	}
	
	private boolean isAPI(HandlerMethod handlerMethod) {
		if(null == handlerMethod) { return false; }
		return handlerMethod.getBeanType().getAnnotation(RestController.class) != null;
	}
}
