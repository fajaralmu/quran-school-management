package com.fajar.schoolmanagement.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomFilter implements javax.servlet.Filter {
  
	
     
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) throws IOException, ServletException {
  
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        boolean isRestEndpoint = req.getMethod().toLowerCase().equals("post");
        log.info("USER PRINCIPAL: {} {}", req.getUserPrincipal(), req.getClass());
        
        if(isRestEndpoint) {
	        log.info("=====================BEGIN API==================");
	        log.info("Content Type: {}", req.getContentType());
	        log.info("Method: {} Uri: {}",req.getMethod(), req.getRequestURI());
	         
        } 
        	
        chain.doFilter(request, response);
        
        if(isRestEndpoint) {
	        log.info("Status: {}", res.getStatus());
	        log.info("Content Type: {}", res.getContentType());
	        log.info("================END API=================");
        }
    }

	 
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	 
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
 
    // other methods
}
