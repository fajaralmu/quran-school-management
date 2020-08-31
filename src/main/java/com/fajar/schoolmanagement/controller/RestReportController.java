package com.fajar.schoolmanagement.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.annotation.Authenticated;
import com.fajar.schoolmanagement.annotation.CustomRequestInfo;
import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.dto.WebRequest;
import com.fajar.schoolmanagement.report.builder.ReportBuilder;
import com.fajar.schoolmanagement.service.report.ReportService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Controller
@Authenticated
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
	@CustomRequestInfo(withRealtimeProgress = true)
	public void monthlygeneralcashflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("daily report {}", request); 
		XSSFWorkbook result = excelReportService.generateGeneralCashflowMonthlyReport(request, httpRequest);

		writeXSSFWorkbook(httpResponse, result, ReportBuilder.randomName("Keuangan_Bulanan"));
	}
	@PostMapping(value = "/thrusdaydonationcashflow", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void thrusdaydonationcashflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("daily report {}", request); 
		XSSFWorkbook result = excelReportService.generateThrusdayDonationCashflowReport(request, httpRequest);

		writeXSSFWorkbook(httpResponse, result, ReportBuilder.randomName("Mutasi_Infaq_Kamis"));
	}
	
	@PostMapping(value = "/thrusdaydonationfundflow", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void thrusdaydonationfundflow(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("thrusdaydonationfundflow report {}", request); 
		XSSFWorkbook result = excelReportService.generateThrusdayDonationFundflowReport(request, httpRequest);

		writeXSSFWorkbook(httpResponse, result, ReportBuilder.randomName("Infaq_Kamis"));
	}
	
	@PostMapping(value = "/studentdonationreport", consumes = MediaType.APPLICATION_JSON_VALUE )
	@CustomRequestInfo(withRealtimeProgress = true)
	public void studentdonationreport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("studentdonationreport {}", request); 
		  
		XSSFWorkbook result = excelReportService.generateYearlyStudentMonthlyDonation(request, httpRequest);

		writeXSSFWorkbook(httpResponse, result, ReportBuilder.randomName("Infaq_Santri"));
	}
	
	@PostMapping(value = "/orphandonationreport", consumes = MediaType.APPLICATION_JSON_VALUE )
	public void orphandonationreport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("studentdonationreport {}", request); 
		
		XSSFWorkbook result = excelReportService.generateDonationOrphanReport(request, httpRequest);

		writeXSSFWorkbook(httpResponse, result, ReportBuilder.randomName("INFAQ_YATIM"));
	}
	
	@PostMapping(value = "/entity", consumes = MediaType.APPLICATION_JSON_VALUE )
	@CustomRequestInfo(withRealtimeProgress = true)
	public void entityreport(@RequestBody WebRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		log.info("entityreport {}", request); 
		
		XSSFWorkbook result = excelReportService.generateEntityReport(request, httpRequest);
		
		writeXSSFWorkbook(httpResponse, result, ReportBuilder.randomName(request.getEntity()));
	}
	
	
	public static void writeXSSFWorkbook(HttpServletResponse httpResponse, XSSFWorkbook xwb, String name) throws Exception{
		httpResponse.setContentType("text/xls");
		httpResponse.setHeader("Content-disposition", "attachment;filename=" + name);

		try (OutputStream outputStream = httpResponse.getOutputStream()) {
			xwb.write(outputStream);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("ERROR writing XSSFWorkBook..");
			e.printStackTrace();
		}
	}
	
	public static void writeFileReponse(HttpServletResponse httpResponse,  byte[] bytes, String name) throws  Exception {
		httpResponse.setHeader("Content-disposition","attachment; filename="+name);
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
	 
		OutputStream out = httpResponse.getOutputStream();
		 
		byte[] buffer= new byte[bytes.length]; // use bigger if you want
		int length = 0;

		while ((length = in.read(buffer)) > 0){
		     out.write(buffer, 0, length);
		}
		in.close();
		out.close();
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
