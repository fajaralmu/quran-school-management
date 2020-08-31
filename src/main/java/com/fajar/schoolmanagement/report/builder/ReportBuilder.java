package com.fajar.schoolmanagement.report.builder;

import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.service.ProgressService;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;
import com.fajar.schoolmanagement.util.StringUtil;

public abstract class ReportBuilder {
	protected final WebConfigService webConfigService;
	protected XSSFSheet xsheet;
	protected XSSFWorkbook xssfWorkbook;
	protected static final String BLANK = "";
	protected static final String DATE_PATTERN = "ddMMyyyy'T'hhmmss-a";
	protected final ReportData reportData;
	protected String reportName;

	// optional
	protected ProgressService progressService;

	public ReportBuilder(WebConfigService configService, ReportData reportData) {
		this.webConfigService = configService;
		this.reportData = reportData;
		init();
	}

	public void setProgressService(ProgressService progressService) {

		this.progressService = progressService;
	}

	protected abstract void init();

	protected String getDateTime() {
		return DateUtil.formatDate(new Date(), DATE_PATTERN);
	}

	protected void sendProgress(double taskProportion, double taskSize, double totalTaskProportion) {
		if (null == progressService)
			return;
		progressService.sendProgress(taskProportion, taskSize, totalTaskProportion, false, reportData.getRequestId());
	}

	public abstract XSSFWorkbook buildReport();

	public static String randomName(String entity) {
		String randomNumber = StringUtil.generateRandomNumber(10);
		return entity+"_"+randomNumber+".xlsx";
	}
}
