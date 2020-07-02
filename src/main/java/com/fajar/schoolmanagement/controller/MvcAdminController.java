package com.fajar.schoolmanagement.controller;

import java.io.IOException;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.annotation.Authenticated;
import com.fajar.schoolmanagement.annotation.CustomRequestInfo;
import com.fajar.schoolmanagement.config.LogProxyFactory;
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
@Authenticated
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
	@CustomRequestInfo(title = "Dashboard", pageUrl = "school/home-page")
	public String menuDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Calendar cal = Calendar.getInstance();

		model.addAttribute("imagePath", webConfigService.getUploadedImagePath());
		model.addAttribute("page", "dashboard");
		model.addAttribute("currentMonth", cal.get(Calendar.MONTH) + 1);
		model.addAttribute("currentYear", cal.get(Calendar.YEAR));

		return basePage;
	}

	@RequestMapping(value = { "/report" })
	@CustomRequestInfo(title = "Report", pageUrl = "school/report-page", stylePaths = { "reportpage" })
	public String reportDashboard(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		model.addAttribute("months", DateUtil.months());
		model.addAttribute("reportMenus", MvcUtil.getReportMenus());

		return basePage;
	}

	@RequestMapping(value = { "/pagesequencesetting" })
	@CustomRequestInfo(title = "Menu Sequence", pageUrl = "school/page-sequence", stylePaths = { "pagesequence" })
	public String pageSequenceSetting(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		model.addAttribute("pages", componentService.getAllPages());

		return basePage;

	}

}
