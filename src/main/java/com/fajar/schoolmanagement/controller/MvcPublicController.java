package com.fajar.schoolmanagement.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.annotation.CustomRequestInfo;
import com.fajar.schoolmanagement.config.LogProxyFactory;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MvcPublicController extends BaseController {

	public MvcPublicController() {
		log.info("---------------------------Mvc Public Controller------------------------------");
	}

	@PostConstruct
	public void init() {
		basePage = webConfigService.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/", "index" })
	@CustomRequestInfo(title = "School Management", pageUrl = "index")
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		String imagebasePath = getFullImagePath(request);
//		model.addAttribute("menus", componentService.getPublicMenus(request)); 
		model.addAttribute("imageUrlList", new ArrayList<>());
		model.addAttribute("page", "main");

		return basePage;

	}

	@RequestMapping(value = { "about" })
	@CustomRequestInfo(title = "About App", pageUrl = "webpage/about-page")
	public String about(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		String imagebasePath = getFullImagePath(request);
//		model.addAttribute("menus", componentService.getPublicMenus(request)); 
		model.addAttribute("page", "about");

		return basePage;

	}

}
