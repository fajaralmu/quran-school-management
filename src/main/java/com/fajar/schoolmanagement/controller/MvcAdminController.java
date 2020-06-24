package com.fajar.schoolmanagement.controller;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.annotation.Authenticated;
import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.service.ComponentService;
import com.fajar.schoolmanagement.util.DateUtil;
import com.fajar.schoolmanagement.util.MvcUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("admin") 
public class MvcAdminController extends BaseController { 
	
	@Autowired
	private ComponentService componentService;

	public MvcAdminController() {
		log.info("-----------------Mvc Admin Controller------------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webConfigService.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/home" })
	@Authenticated
	public String menuDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Calendar cal = Calendar.getInstance();
		
		setActivePage(request );
 
		model.addAttribute("imagePath", webConfigService.getUploadedImagePath());
		model.addAttribute("title", "App::Dashboard");
		model.addAttribute("pageUrl", "school/home-page");
		model.addAttribute("page", "dashboard");
		model.addAttribute("currentMonth", cal.get(Calendar.MONTH) + 1);
		model.addAttribute("currentYear", cal.get(Calendar.YEAR));
		 
		return basePage;
	}

	@RequestMapping(value = { "/report" })
	@Authenticated
	public String reportDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		model.addAttribute("title", "App::Report");
		model.addAttribute("pageUrl", "school/report-page");
		model.addAttribute("months", DateUtil.months());
		model.addAttribute("reportMenus", MvcUtil.getReportMenus()); 
		addStylePath(model, "reportpage");
		return basePage; 
	}
	
	@RequestMapping(value = { "/pagesequencesetting" })
	@Authenticated
	public String pagesequencesetting(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		model.addAttribute("title", "App::Page Sequence");
		model.addAttribute("pageUrl", "school/page-sequence"); 
		model.addAttribute("pages", componentService.getAllPages()); 
		addStylePath(model, "pagesequence");
		return basePage;
		
	}
	
	
 
}
