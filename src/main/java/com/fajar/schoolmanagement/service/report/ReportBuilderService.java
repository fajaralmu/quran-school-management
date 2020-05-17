package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.util.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.util.FileUtil.getFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.CurrencyCell;
import com.fajar.schoolmanagement.util.DateUtil;
import com.fajar.schoolmanagement.util.ExcelReportUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportBuilderService {
	private static final String BLANK = "";
	@Autowired
	private WebConfigService webConfigService;

	private String reportPath;

	@PostConstruct
	public void init() {
		this.reportPath = webConfigService.getReportPath();
	}

	public File generateMonthlyGeneralCashflow(ReportData reportData) {

		Filter filter = reportData.getFilter();
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Daily-" + filter.getMonth() + "-" + filter.getYear();

		String reportName = reportPath + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);
		writeMonthlyGeneralCashflow(xsheet, reportData);

		File file = getFile(xwb, reportName);
		return file;
	}

	private void writeMonthlyGeneralCashflow(XSSFSheet xsheet, ReportData reportData) {
		
		Filter filter = reportData.getFilter();
		Integer monthDays = getMonthDays(filter);
		int month = filter.getMonth();

		Map<Integer, List<BaseEntity>> mappedFunds = sortFinancialEntityByDayOfMonth(reportData.getFunds(), monthDays);
		Map<Integer, List<BaseEntity>> mappedSpendings = sortFinancialEntityByDayOfMonth(reportData.getSpendings(), monthDays);
		CashBalance initialBalance = reportData.getInitialBalance();
		
		int currentRow = 0;
		int columnOffset = 1;
		
		/**
		 * build fund table
		 */
		Object[] headerValues = { "Tanggal", "Keterangan", "Debet", "Total", "Tanggal", "Keterangan", "Kredit", "Total" };
		createRow(xsheet, currentRow , columnOffset, headerValues);
		
		//initial balance row
		int fundRow = currentRow++;
		int spendingRow = fundRow;
		
		//funds
		Object[] initialBalanceRow = {"1/"+month, "Saldo Awal", "", curr(initialBalance.getActualBalance())};
		createRow(xsheet, fundRow, columnOffset, initialBalanceRow );
		fundRow++;  
		long summaryFund  = writeMonthlyCashflow(fundRow, mappedFunds, xsheet, columnOffset);  
		
		//spending
		long summarySpending = writeMonthlyCashflow(spendingRow, mappedSpendings, xsheet, 4);
		
		log.info("Spending Row: {}", spendingRow);
		log.info("Fund Row: {}", fundRow);
		log.info("Total Fund: {}, initialBalance: {}", summaryFund, initialBalance.getActualBalance());
	}
	
	private static long writeMonthlyCashflow(int currentRow, Map<Integer, List<BaseEntity>> mappedCashflow, XSSFSheet xsheet, int columnOffset) {
		long summaryCashflow = 0L;
		for(Integer day : mappedCashflow.keySet()) {
			List<BaseEntity> funds = mappedCashflow.get(day);
			for (BaseEntity fund : funds) {
				
				int month = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.MONTH) + 1;
				currentRow++;
				Object[] fundRowValues = {day+"/"+month, fund.getTransactionName(), curr(fund.getTransactionNominal()), ""};
				createRow(xsheet, currentRow, columnOffset, fundRowValues );
				summaryCashflow += fund.getTransactionNominal();
			}
		}
		
		return summaryCashflow;
	}
	
	private int getMonthDays(Filter filter) {
		int year = filter.getYear();
		int monthIndex = filter.getMonth() - 1;
		Integer monthDays = DateUtil.getMonthsDay(year)[monthIndex];
		return monthDays;
	}

	private Map<Integer, List<BaseEntity>> sortFinancialEntityByDayOfMonth(List<BaseEntity> funds, int monthDays) {
		Map<Integer, List<BaseEntity>> mappedFunds = fillMapKeysWithDays(monthDays);

		for (int i = 0; i < funds.size(); i++) {
			BaseEntity fund = funds.get(i); 
			
			int transactionDay = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.DAY_OF_MONTH); 
			mappedFunds.get(transactionDay).add(fund);
		}
		
		return mappedFunds;
	}
	 

	private Map<Integer, List<BaseEntity>> fillMapKeysWithDays(int dayCount) {
		Map<Integer, List<BaseEntity>> map = new HashMap<Integer, List<BaseEntity>>();
		for (int day = 1; day <= dayCount; day++) {
			map.put(day, new ArrayList<>());
		}
		return map;
	}

	public File getEntityReport(List<BaseEntity> entities, EntityProperty entityProperty) {
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = entityProperty.getEntityName();

		String reportName = reportPath + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);

		Object[] entityValues = ExcelReportUtil.getEntitiesTableValues(entities, entityProperty);
		ExcelReportUtil.createTable(xsheet, entityProperty.getElements().size() + 1, 2, 2, entityValues);

		File file = getFile(xwb, reportName);
		return file;
	}
	
	public static CurrencyCell curr(long value) {
		return new CurrencyCell(value);
	}

}
