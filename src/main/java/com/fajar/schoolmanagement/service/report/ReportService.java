package com.fajar.schoolmanagement.service.report;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.dto.WebRequest;
import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.service.EntityService;
import com.fajar.schoolmanagement.service.transaction.TransactionService;
import com.fajar.schoolmanagement.util.CollectionUtil;

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
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public File generateGeneralCashflowMonthlyReport(WebRequest webRequest) {
		
		ReportData transactionData = transactionService.getMonthlyGeneralCashflow(webRequest.getFilter()); 
		
		return cashflowReportService.generateMonthlyGeneralCashflow(transactionData);
	}
	
	public File generateThrusdayDonationCashflowReport(WebRequest webRequest) {
		ReportData transactionData = transactionService.getYearlyThrusdayDonationCashflow(webRequest.getFilter());
		
		return thrusdayDonationReportService.generateThrusdayCashflowReport(transactionData);
	}
	
	public File generateYearlyStudentMonthlyDonation(WebRequest webRequest) {
		
		ReportData transactionData = transactionService.getYearlyMonthlyDonationCashflow(webRequest.getFilter());
		List<BaseEntity> studentList = getAllStudents();
		List<Student> convertList = CollectionUtil.convertList(studentList);
		File result = studentDonationReportService.generateMonthlyStudentDonationReport(transactionData, convertList);
		return result ;
	}
 
	private List<BaseEntity> getAllStudents() {
		List<BaseEntity> students = entityService.findAll(Student.class);
		return students;
	}

	public File generateEntityReport(WebRequest request) { 
//		request.getFilter().setLimit(0);
		WebResponse response = entityService.filter(request);
		
		File file = entityReportService.generateEntityReport(response.getEntities(), response.getEntityClass());
		return file ;
	} 

	public File generateThrusdayDonationFundflowReport(WebRequest webRequest) { 
		ReportData transactionData = transactionService.getYearlyThrusdayDonationCashflow(webRequest.getFilter());
		
		return thrusdayDonationReportService.generateThrusdayDonationReport(transactionData);
	}
	
	public File generateDonationOrphanReport(WebRequest request) {
		ReportData transactionData = transactionService.getDonationOrphanReport(request.getFilter());
		
		return orphanDonationReportService.generateOrphanDonationReport(transactionData);
	}

}
