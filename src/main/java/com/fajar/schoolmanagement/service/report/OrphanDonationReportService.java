package com.fajar.schoolmanagement.service.report;
 

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.report.builder.FundAndSpendingFlowReportMonthly;
import com.fajar.schoolmanagement.service.ProgressService;
import com.fajar.schoolmanagement.service.WebConfigService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrphanDonationReportService {
	
	@Autowired
	private WebConfigService webConfigService; 
	@Autowired
	private ProgressService progressService;

	public XSSFWorkbook generateOrphanDonationReport(ReportData reportData) { 
		reportData.setReportName("DANA_YATIM");
		
		FundAndSpendingFlowReportMonthly reportBuilder = new FundAndSpendingFlowReportMonthly(reportData, webConfigService, progressService);
		XSSFWorkbook file = reportBuilder.buildReport();
		
		log.info("generated: OrphanDonationReport");
		return file;
		
	}

}
