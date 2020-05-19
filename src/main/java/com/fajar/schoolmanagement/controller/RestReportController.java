package com.fajar.schoolmanagement.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.dto.WebRequest;
import com.fajar.schoolmanagement.service.report.ReportService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Controller
@RequestMapping("/api/report")
@Slf4j
public class RestReportController {
	
	@Autowired
	private ReportService excelReportService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	@PostMapping(value = "/monthlygeneralcashflow", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void monthlygeneralcashflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("daily report {}", request);
//		if(!userSessionService.hasSession(httpRequest)) {
//			return ShopApiResponse.failedResponse();
//		}
		  
		File result = excelReportService.generateGeneralCashflowMonthlyReport(request);

		writeFileReponse(httpResponse, result);
	}
	@PostMapping(value = "/thrusdaydonationcashflow", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void thrusdaydonationcashflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("daily report {}", request);
//		if(!userSessionService.hasSession(httpRequest)) {
//			return ShopApiResponse.failedResponse();
//		}
		  
		File result = excelReportService.generateThrusdayDonationCashflowReport(request);

		writeFileReponse(httpResponse, result);
	}
	
	@PostMapping(value = "/thrusdaydonationfundflow", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void thrusdaydonationfundflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("daily report {}", request);
//		if(!userSessionService.hasSession(httpRequest)) {
//			return ShopApiResponse.failedResponse();
//		}
		  
		File result = excelReportService.generateThrusdayDonationFundflowReport(request);

		writeFileReponse(httpResponse, result);
	}
	
	@PostMapping(value = "/studentdonationreport", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void studentdonationreport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("studentdonationreport {}", request);
//		if(!userSessionService.hasSession(httpRequest)) {
//			return ShopApiResponse.failedResponse();
//		}
		  
		File result = excelReportService.generateYearlyStudentMonthlyDonation(request);

		writeFileReponse(httpResponse, result);
	}
	
	@PostMapping(value = "/entity", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void entityreport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("entityreport {}", request);
//		if(!userSessionService.hasSession(httpRequest)) {
//			return ShopApiResponse.failedResponse();
//		}
		
		File result = excelReportService.generateEntityReport(request);
		
		writeFileReponse(httpResponse, result);
	}
	
	public static void writeFileReponse(HttpServletResponse httpResponse, File file) throws  Exception {
		httpResponse.setHeader("Content-disposition","attachment; filename="+file.getName());
		FileInputStream in = new FileInputStream(file);
		OutputStream out = httpResponse.getOutputStream();

		byte[] buffer= new byte[8192]; // use bigger if you want
		int length = 0;

		while ((length = in.read(buffer)) > 0){
		     out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}
	
	 
	 

}
