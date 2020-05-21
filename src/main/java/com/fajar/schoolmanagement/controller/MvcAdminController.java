package com.fajar.schoolmanagement.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.util.DateUtil;

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

	public MvcAdminController() {
		log.info("-----------------Mvc Admin Controller------------------");
	}

	@PostConstruct
	private void init() {
		this.basePage = webConfigService.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/home" })
	public String menuDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Calendar cal = Calendar.getInstance();
		
		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		
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
	public String reportDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		model.addAttribute("title", "App::Report");
		model.addAttribute("pageUrl", "school/report-page");
		model.addAttribute("months", DateUtil.months());
		model.addAttribute("reportMenus",getReportMenus());
		
		
		return basePage;
		
	}
	
	public List<Menu> getReportMenus(){
		
		
		List<Menu> menus = new ArrayList<Menu>();
		menus.add(getReportMenu("Keuangan Bulanan", "monthlygeneralcashflow", "Bulan", "Tahun"));
		menus.add(getReportMenu("Infaq Bulanan Siswa", "studentdonationreport", "Tahun"));
		menus.add(getReportMenu("Mutasi Infaq Kamis", "thrusdaydonationcashflow", "Tahun"));
		menus.add(getReportMenu("Pemasukan Infaq Kamis", "thrusdaydonationfundflow", "Tahun"));
		menus.add(getReportMenu("Dana Yatim", "orphandonationreport", "Tahun"));
		return menus ;
	}

	private Menu getReportMenu(String title, String link, String... mandatoryFields) {
		Menu menu = new Menu();
		menu.setName(title);
		menu.setUrl(link);
		String mandatoryInfo = String.join(" and ", mandatoryFields);
		String desc = "Please select "+mandatoryInfo+" to process this report";
		menu.setDescription(desc);
		return menu;
	}
 
}
