package com.fajar.schoolmanagement.report.builder;

import java.io.File;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;

public abstract class ReportBuilder {
	protected final WebConfigService webConfigService;
	protected XSSFSheet xsheet;
	protected XSSFWorkbook xssfWorkbook;
	protected static final String BLANK = "";
	protected static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a"; 
	protected final ReportData reportData;
	protected String reportName;
	
	public ReportBuilder(WebConfigService configService, ReportData reportData) {
		this.webConfigService = configService;
		this.reportData = reportData;
	}

	protected String getDateTime() {
		return DateUtil.formatDate(new Date(), DATE_PATTERN);
	}
	
	public abstract File buildReport( );
}
