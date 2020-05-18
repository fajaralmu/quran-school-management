package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.curr;
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
import com.fajar.schoolmanagement.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CashflowReportService {
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
		String sheetName = "Laporan_Bulanan-" + filter.getMonth() + "-" + filter.getYear();

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
		
		int currentRow = 1;
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
		fundRow++;
		Object[] initialBalanceRow = {"1/"+month, "Saldo Awal", "", curr(initialBalance.getActualBalance())};
		createRow(xsheet, fundRow, columnOffset, initialBalanceRow );
		long summaryFund  = writeMonthlyCashflowTable(fundRow, mappedFunds, xsheet, columnOffset);  
		int fundRowCount = 1 + getCashflowItemCount(mappedFunds); //one for intiial Balance
		
		//spending
		long summarySpending = writeMonthlyCashflowTable(spendingRow, mappedSpendings, xsheet, 5);
		int spendingRowCount = getCashflowItemCount(mappedSpendings);

		int rowForTotal = fundRowCount > spendingRowCount ? fundRowCount + 2 : spendingRowCount + 2;
		
		//rowTotal
		long grandTotalFund = summaryFund + initialBalance.getActualBalance();
		long grandTotalBalance = grandTotalFund - summarySpending;
		createRow(xsheet, rowForTotal, 1, "", "", curr(summaryFund), curr(grandTotalFund), "","", curr(summarySpending), curr(grandTotalBalance));
		
		log.info("Spending Row: {}", spendingRowCount);
		log.info("Fund Row: {}", fundRowCount);
		log.info("Total Fund: {}, initialBalance: {}", summaryFund, initialBalance.getActualBalance());
	}
	
	/**
	 * get list size in the map<integer, list>
	 * @param mappedCashflow
	 * @return
	 */
	private static int getCashflowItemCount( Map<Integer, List<BaseEntity>> mappedCashflow) {
		int count = 0;
		for(Integer day:mappedCashflow.keySet()) {
			count += mappedCashflow.get(day).size();
		}
		return count;
	}
	
	private static long writeMonthlyCashflowTable(int currentRow, Map<Integer, List<BaseEntity>> mappedCashflow, XSSFSheet xsheet, int columnOffset) {
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
		Map<Integer, List<BaseEntity>> mappedFunds = fillMapKeysWithNumber(monthDays);

		for (int i = 0; i < funds.size(); i++) {
			BaseEntity fund = funds.get(i); 
			
			int transactionDay = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.DAY_OF_MONTH); 
			mappedFunds.get(transactionDay).add(fund);
		}
		
		return mappedFunds;
	} 
	private Map<Integer, List<BaseEntity>> sortFinancialEntityByMonth(List<BaseEntity> funds ) {
		Map<Integer, List<BaseEntity>> mappedFunds = fillMapKeysWithNumber(12);

		for (int i = 0; i < funds.size(); i++) {
			BaseEntity fund = funds.get(i); 
			
			int transactionDay = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.MONTH) + 1; 
			mappedFunds.get(transactionDay).add(fund);
		}
		
		return mappedFunds;
	} 

	private Map<Integer, List<BaseEntity>> fillMapKeysWithNumber(int dayCount) {
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
	
	// ----------------- Thrusday Report -------------------//

	public File generateThrusdayCashflowReport(ReportData transactionData) {
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Infaq_Kamis";

		String reportName = reportPath + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName); 

		writeThrusdayCashflowReport(xsheet, transactionData);
		
		File file = getFile(xwb, reportName);
		return file;
	}

	private void writeThrusdayCashflowReport(XSSFSheet xsheet, ReportData reportData) {
		
		Map<Integer, List<BaseEntity>> mappedFunds = sortFinancialEntityByMonth(reportData.getFunds());
		Map<Integer, List<BaseEntity>> mappedSpendings = sortFinancialEntityByMonth(reportData.getSpendings());
		CashBalance initialBalance = reportData.getInitialBalance();
		
		int currentRow = 1;
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
		fundRow++;
		Object[] initialBalanceRow = {"1/1", "Saldo Awal", "", curr(initialBalance.getActualBalance())};
		createRow(xsheet, fundRow, columnOffset, initialBalanceRow );
		long summaryFund  = writeMonthlyCashflowTable(fundRow, mappedFunds, xsheet, columnOffset);  
		int fundRowCount = 1 + getCashflowItemCount(mappedFunds); //one for intiial Balance
		
		//spending
		long summarySpending = writeMonthlyCashflowTable(spendingRow, mappedSpendings, xsheet, 5);
		int spendingRowCount = getCashflowItemCount(mappedSpendings);

		int rowForTotal = fundRowCount > spendingRowCount ? fundRowCount + 2 : spendingRowCount + 2;
		
		//rowTotal
		long grandTotalFund = summaryFund + initialBalance.getActualBalance();
		long grandTotalBalance = grandTotalFund - summarySpending;
		createRow(xsheet, rowForTotal, 1, "", "", curr(summaryFund), curr(grandTotalFund), "","", curr(summarySpending), curr(grandTotalBalance));
		
		log.info("Spending Row: {}", spendingRowCount);
		log.info("Fund Row: {}", fundRowCount);
		log.info("Total Fund: {}, initialBalance: {}", summaryFund, initialBalance.getActualBalance());
	}  

}
