package com.fajar.schoolmanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.config.LogProxyFactory;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MvcPublicController extends BaseController{ 
	  
	
	public MvcPublicController() {
		log.info("---------------------------Mvc Public Controller------------------------------");
	}
	
	@PostConstruct
	public void init() {
		basePage = webAppConfiguration.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/", "index" })
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String imagebasePath = getFullImagePath(request);
//		model.addAttribute("menus", componentService.getPublicMenus(request));
		model.addAttribute("title", "School Application");
		model.addAttribute("pageUrl", "index"); 
		model.addAttribute("imageUrlList", new ArrayList<>());
		model.addAttribute("page", "main"); 
		
		return basePage;

	}
	
	
	 
	

}
