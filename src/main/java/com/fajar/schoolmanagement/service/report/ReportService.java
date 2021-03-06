package com.fajar.schoolmanagement.service.report;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.dto.WebRequest;
import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.service.EntityService;
import com.fajar.schoolmanagement.service.ProgressService;
import com.fajar.schoolmanagement.service.transaction.TransactionService;
import com.fajar.schoolmanagement.util.CollectionUtil;
import com.fajar.schoolmanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportService {
	
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private CashflowReportService cashflowReportService;
	@Autowired
	private EntityService entityService;
	@Autowired
	private StudentDonationReportService studentDonationReportService;
	@Autowired
	private ThrusdayDonationReportService thrusdayDonationReportService;
	@Autowired
	private OrphanDonationReportService orphanDonationReportService;
	@Autowired
	private EntityReportService entityReportService;
	@Autowired
	private ProgressService progressService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public XSSFWorkbook generateGeneralCashflowMonthlyReport(WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("generateGeneralCashflowMonthlyReport");
		
		String requestId = SessionUtil.getPageRequestId(httpRequest);
		ReportData transactionData = transactionService.getMonthlyGeneralCashflow(webRequest.getFilter()); 
		transactionData.setRequestId(requestId);
		progressService.sendProgress(1, 1, 20, requestId);
		
		return cashflowReportService.generateMonthlyGeneralCashflow(transactionData);
	}
	
	public XSSFWorkbook generateThrusdayDonationCashflowReport(WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("generateThrusdayDonationCashflowReport");
		
		String requestId = SessionUtil.getPageRequestId(httpRequest);
		ReportData transactionData = transactionService.getYearlyThrusdayDonationCashflow(webRequest.getFilter());
		transactionData.setRequestId(requestId);
		progressService.sendProgress(1, 1, 20, requestId);
		
		return thrusdayDonationReportService.generateThrusdayCashflowReport(transactionData);
	}
	
	public XSSFWorkbook generateYearlyStudentMonthlyDonation(WebRequest webRequest, HttpServletRequest httpRequest) {
		log.info("generateYearlyStudentMonthlyDonation");
		String requestId = SessionUtil.getPageRequestId(httpRequest);
		
		ReportData transactionData = transactionService.getYearlyMonthlyDonationCashflow(webRequest.getFilter());
		transactionData.setRequestId(requestId);
		progressService.sendProgress(1, 1, 10, requestId);
		
		List<Student> studentList = getAllStudents();
		List<BaseEntity> convertedList = CollectionUtil.convertList(studentList);
		transactionData.setEntities(convertedList);
		progressService.sendProgress(1, 1, 10, requestId); 
		
		
		XSSFWorkbook result = studentDonationReportService.generateMonthlyStudentDonationForOneYearReport(transactionData);
		return result ;
	}
 
	private List<Student> getAllStudents() {
		 List<Student> students = entityService.findAll(Student.class);
		return students;
	}

	public XSSFWorkbook generateEntityReport(WebRequest request, HttpServletRequest httpRequest) throws Exception { 
		log.info("generateEntityReport");
//		request.getFilter().setLimit(0);
		String requestId = SessionUtil.getPageRequestId(httpRequest); 
		
		WebResponse response = entityService.filter(request);
		
		progressService.sendProgress(1, 1, 20, true, requestId);
		
		XSSFWorkbook file = entityReportService.getEntityReport(response.getEntities(), response.getEntityClass(), httpRequest); 
		
		return file ;
	} 

	public XSSFWorkbook generateThrusdayDonationFundflowReport(WebRequest webRequest, HttpServletRequest httpRequest) { 
		log.info("generateThrusdayDonationFundflowReport");
		
		String requestId = SessionUtil.getPageRequestId(httpRequest);
		ReportData transactionData = transactionService.getYearlyThrusdayDonationCashflow(webRequest.getFilter());
		transactionData.setRequestId(requestId);
		
		progressService.sendProgress(1, 1, 20, true, requestId);
		
		return thrusdayDonationReportService.generateThrusdayDonationReport(transactionData);
	}
	
	public XSSFWorkbook generateDonationOrphanReport(WebRequest request, HttpServletRequest httpRequest) {
		log.info("generateDonationOrphanReport");
		
		String requestId = SessionUtil.getPageRequestId(httpRequest);
		ReportData transactionData = transactionService.getDonationOrphanReport(request.getFilter());
		transactionData.setRequestId(requestId);
		
		progressService.sendProgress(1, 1, 20, true, requestId);
		
		return orphanDonationReportService.generateOrphanDonationReport(transactionData);
	}

}
