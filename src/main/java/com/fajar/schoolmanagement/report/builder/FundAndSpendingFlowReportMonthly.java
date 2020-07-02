package com.fajar.schoolmanagement.report.builder;

import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.curr;
import static com.fajar.schoolmanagement.util.DateUtil.MONTH_NAMES;
import static com.fajar.schoolmanagement.util.DateUtil.getCalendarItem;
import static com.fajar.schoolmanagement.util.FileUtil.getFile;
import static java.util.Calendar.DAY_OF_MONTH;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.FinancialEntity;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.service.report.ReportMappingUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * report containing fund flow and spending flow
 * @author Republic Of Gamers
 *
 */
@Slf4j
public class FundAndSpendingFlowReportMonthly extends ReportBuilder{

	 
	final Map<Integer, List<FinancialEntity>> mappedFunds;
	final Map<Integer, List<FinancialEntity>> mappedSpendings;
	final Object[] HEADER_VALUES = { "Tanggal", "Keterangan", "Debet", "Total", "Tanggal", "Keterangan", "Kredit", "Total" };

	public FundAndSpendingFlowReportMonthly(ReportData reportData, WebConfigService webConfigService) {
		super(webConfigService, reportData);
		mappedFunds = ReportMappingUtil.sortFinancialEntityByMonth(reportData.getFunds());
		mappedSpendings = ReportMappingUtil.sortFinancialEntityByMonth(reportData.getSpendings()); 
	}
	
	protected void init() {
		log.info("will generate: OrphanDonationReport"); 
		
		reportName = getReportName();
		xssfWorkbook = new XSSFWorkbook();
		xsheet = xssfWorkbook.createSheet(reportData.getReportName());
		
	}

	public String getReportName() {  
		String time = ReportMappingUtil.getReportDateString();
		String sheetName = reportData.getReportName(); 
		
		return webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
	}

	public void writeReport() {

		CashBalance initialBalance = reportData.getInitialBalance();

		int currentRow = 1;
		int columnOffset = 1;

		/**
		 * build fund table
		 */
		createHeader(currentRow, columnOffset);

		// initial balance row
		currentRow++;

		// funds
		writeInitialBalance(currentRow, columnOffset);
		final int fundRowCount = writeMonthlyCashflowTable(currentRow, mappedFunds, mappedSpendings, columnOffset);
		final long summaryFund = getSummary(reportData.getFunds()); // one for initial Balance

		// spending
		final int spendingRowCount = writeMonthlyCashflowTable(currentRow, mappedSpendings, mappedFunds, 5);
		final long summarySpending = getSummary(reportData.getSpendings());

		int rowForTotal = fundRowCount > spendingRowCount ? fundRowCount + 3 : spendingRowCount + 3;
//		
		// rowTotal
		long grandTotalFund = summaryFund + initialBalance.getActualBalance();
		long grandTotalBalance = grandTotalFund - summarySpending;
		createRow(xsheet, rowForTotal, 1, "", "", curr(summaryFund), curr(grandTotalFund), "", "",
				curr(summarySpending), curr(grandTotalBalance));

		log.info("Fund Row: {}", fundRowCount);
		log.info("grandTotalFund: {}, summarySpending: {}, grandTotalBalance: {}", grandTotalFund, summarySpending,
				grandTotalBalance);
		
		ExcelReportUtil.setAllBorder(xsheet, 1, columnOffset, HEADER_VALUES.length, rowForTotal + 1);
	}

	private void createHeader(int currentRow, int columnOffset) { 
		createRow(xsheet, currentRow, columnOffset, HEADER_VALUES);
	}

	private void writeInitialBalance(int currentRow, int columnOffset) {
		CashBalance initialBalance = reportData.getInitialBalance();
		Object[] initialBalanceRow = { "1/1", "Saldo Awal", "", curr(initialBalance.getActualBalance()), "", "", "",
				"" };
		createRow(xsheet, currentRow, columnOffset, initialBalanceRow);
	}

	private long getSummary(List<FinancialEntity> cashflow) {
		long summary = 0L;
		for (FinancialEntity baseEntity : cashflow) {
			summary += baseEntity.getTransactionNominal();
		}
		return summary;
	}

	public int writeMonthlyCashflowTable(final int currentRow, Map<Integer, List<FinancialEntity>> mappedCashflow,
			Map<Integer, List<FinancialEntity>> comparedCashflow, int columnOffset) {

		int currentCashflowRow = currentRow;
		int totalRow = 0;

		for (Integer currentMonth : mappedCashflow.keySet()) {
			currentCashflowRow++;
			createRow(xsheet, currentCashflowRow, columnOffset, MONTH_NAMES[currentMonth - 1], "", "", "");

			List<FinancialEntity> cashflows = mappedCashflow.get(currentMonth);
			List<FinancialEntity> comparedCashflowInCurrentMonth = comparedCashflow.get(currentMonth);

			int cashflowSize = cashflows.size();
			int comparedCashflowSize = comparedCashflowInCurrentMonth.size();
			int totalRowDedicatedForCurrentMonth = cashflowSize > comparedCashflowSize ? cashflowSize
					: comparedCashflowSize;
			int rowCounter = 0;

			for (FinancialEntity cashflow : cashflows) {
 
				currentCashflowRow++;  
				writeCashflowRow(cashflow,  currentCashflowRow, columnOffset); 
				rowCounter++;
			}

			if (rowCounter < totalRowDedicatedForCurrentMonth) {
				currentCashflowRow++;
				int gap = totalRowDedicatedForCurrentMonth - rowCounter;
				for (int i = 0; i < gap; i++) {
					createRow(xsheet, currentCashflowRow, columnOffset, "", "", "", "");
					rowCounter++;
					if (i < gap - 1)
						currentCashflowRow++;
				}
			}
			totalRow += rowCounter;
		}

		return totalRow + mappedCashflow.keySet().size();
	}

	private void writeCashflowRow(FinancialEntity fund, int currentCashflowRow, int columnOffset) {
		
		int day = getCalendarItem(fund.getTransactionDate(), DAY_OF_MONTH);
		int month = getCalendarItem(fund.getTransactionDate(), Calendar.MONTH )+1;
		log.info("fund.getTransactionDate(): {}, month: {}", fund.getTransactionDate(), month);
		Object[] cashflowRowValues = { day + "/" + month, fund.getTransactionName(),
				curr(fund.getTransactionNominal()), "" };
		createRow(xsheet, currentCashflowRow, columnOffset, cashflowRowValues);
	}

	@Override
	public File buildReport() {
		writeReport();
		File file = getFile(xssfWorkbook, reportName);
		return file;
	}
	
	 

}
