package com.fajar.schoolmanagement.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.annotation.Authenticated;
import com.fajar.schoolmanagement.annotation.CustomRequestInfo;
import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.entity.Page;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("webapp")
@Slf4j
public class MvcPagesController extends BaseController{  
	
	public MvcPagesController() {
		log.info("---------------------------Mvc Public Controller------------------------------");
	}
	
	@PostConstruct
	public void init() {
		basePage = webConfigService.getBasePage();
		LogProxyFactory.setLoggers(this);
	}
 
	 
	@RequestMapping(value = { "/page/{code}" })
	@Authenticated
	@CustomRequestInfo(pageUrl = "school/master-common-page")
	public String page(@PathVariable(name = "code") String code,  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, NotFoundException {
		
		Page page = componentService.getPage(code, request);
		
		if(null == page) {
			sendRedirect(response, request.getContextPath() + "/account/login");
			return basePage;
		} 
		model.addAttribute("page", page); 
		model.addAttribute("title", page.getName());
		return basePage;

	} 
	

}
