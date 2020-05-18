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
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.service.EntityService;
import com.fajar.schoolmanagement.service.transaction.TransactionService;
import com.fajar.schoolmanagement.util.CollectionUtil;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportService {
	
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ReportBuilderService reportBuilderService;
	@Autowired
	private EntityService entityService;
	@Autowired
	private StudentDonationReportService studentDonationReportService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public File generateGeneralCashflowMonthlyReport(WebRequest webRequest) {
		
		ReportData transactionData = transactionService.getMonthlyGeneralCashflow(webRequest.getFilter()); 
		
		return reportBuilderService.generateMonthlyGeneralCashflow(transactionData);
	}
	
	public File generateYearlyStudentMonthlyDonation(WebRequest webRequest) {
		
		ReportData transactionData = transactionService.getYearlyMonthlyDonationCashflow(webRequest.getFilter());
		List<BaseEntity> studentList = entityService.findAll(Student.class);
		File result = studentDonationReportService.generateMonthlyStudentDonationReport(transactionData, CollectionUtil.convertList(studentList));
		return result ;
	}

	public File getEntityReport(List<BaseEntity> entities, Class<? extends BaseEntity> entityClass) {
		log.info("entities count: {}", entities.size());
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityClass, null);
		
		File result = reportBuilderService.getEntityReport(entities, entityProperty);
		return result;
	}

	public File generateEntityReport(WebRequest request) { 
//		request.getFilter().setLimit(0);
		WebResponse response = entityService.filter(request);
		
		File file = getEntityReport(response.getEntities(), response.getEntityClass());
		return file ;
	}

}
