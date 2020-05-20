package com.fajar.schoolmanagement.service.report;
 

import static com.fajar.schoolmanagement.util.FileUtil.getFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrphanDonationReportService {
	
	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ThrusdayDonationReportService thrusdayDonationReportService;

	public File generateOrphanDonationReport(ReportData transactionData) {
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Dana_Yatim";

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);

		writeOrphanDonationCashflowReport(xsheet, transactionData);

		File file = getFile(xwb, reportName);
		return file;
	}

	private void writeOrphanDonationCashflowReport(XSSFSheet xsheet, ReportData reportData) {
//		Map<Integer, List<BaseEntity>> mappedFunds = CashflowReportService
//				.sortFinancialEntityByMonth(reportData.getFunds());
//		Map<Integer, List<BaseEntity>> mappedSpendings = CashflowReportService
//				.sortFinancialEntityByMonth(reportData.getSpendings());
//		CashBalance initialBalance = reportData.getInitialBalance();

		thrusdayDonationReportService.writeThrusdayCashflowReport(xsheet, reportData);
	}

}
