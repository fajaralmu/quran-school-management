package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.curr;
import static com.fajar.schoolmanagement.util.FileUtil.getFile;

import java.io.File;
import java.util.Calendar;
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

public class ThrusdayDonationReportService {

	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private CashflowReportService cashflowReportService;

	public File generateThrusdayCashflowReport(ReportData transactionData) {
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Infaq_Kamis";

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);

		writeThrusdayCashflowReport(xsheet, transactionData);

		File file = getFile(xwb, reportName);
		return file;
	}

	private void writeThrusdayCashflowReport(XSSFSheet xsheet, ReportData reportData) {

		Map<Integer, List<BaseEntity>> mappedFunds = CashflowReportService
				.sortFinancialEntityByMonth(reportData.getFunds());
		Map<Integer, List<BaseEntity>> mappedSpendings = CashflowReportService
				.sortFinancialEntityByMonth(reportData.getSpendings());
		CashBalance initialBalance = reportData.getInitialBalance();

		int currentRow = 1;
		int columnOffset = 1;

		/**
		 * build fund table
		 */
		Object[] headerValues = { "Tanggal", "Keterangan", "Debet", "Total", "Tanggal", "Keterangan", "Kredit",
				"Total" };
		createRow(xsheet, currentRow, columnOffset, headerValues);

		// initial balance row
//		int fundRows = currentRow++; 

		// funds
		currentRow++;
		Object[] initialBalanceRow = { "1/1", "Saldo Awal", "", curr(initialBalance.getActualBalance()) };
		createRow(xsheet, currentRow, columnOffset, initialBalanceRow);
		final long summaryFund = writeMonthlyCashflowTable(currentRow, mappedFunds, mappedSpendings, xsheet,
				columnOffset);
		final int fundRowCount = 1 + CashflowReportService.getCashflowItemCount(mappedFunds); // one for intiial Balance

		// spending
		final long summarySpending = writeMonthlyCashflowTable(currentRow, mappedSpendings, mappedFunds, xsheet, 5);
		final int spendingRowCount = CashflowReportService.getCashflowItemCount(mappedSpendings);

		int rowForTotal = fundRowCount > spendingRowCount ? fundRowCount + 2 : spendingRowCount + 2;
//		
		// rowTotal
		long grandTotalFund = summaryFund + initialBalance.getActualBalance();
		long grandTotalBalance = grandTotalFund - summarySpending;
		createRow(xsheet, rowForTotal, 1, "", "", curr(summaryFund), curr(grandTotalFund), "", "",
				curr(summarySpending), curr(grandTotalBalance));

//		log.info("Spending Row: {}", spendingRowCount);
		log.info("Fund Row: {}", fundRowCount);
		log.info("grandTotalFund: {}, summarySpending: {}, grandTotalBalance: {}", grandTotalFund, summarySpending, grandTotalBalance);
	}

	private static long writeMonthlyCashflowTable(final int currentRow, Map<Integer, List<BaseEntity>> mappedCashflow,
			Map<Integer, List<BaseEntity>> comparedCashflow, XSSFSheet xsheet, int columnOffset) {
		long summaryCashflow = 0L;
		int currentCashflowRow = currentRow;
		int currentSpendingRow = currentRow;

		/**
		 * ================= fund ================ *
		 */
		for (Integer currentMonth : mappedCashflow.keySet()) {
			List<BaseEntity> cashflows = mappedCashflow.get(currentMonth);
			List<BaseEntity> comparedCashflowInCurrentMonth = comparedCashflow.get(currentMonth);

			int cashflowSize = cashflows.size();
			int comparedCashflowSize = comparedCashflowInCurrentMonth.size();
			int totalRowDedicatedForCurrentMonth = cashflowSize > comparedCashflowSize ? cashflowSize
					: comparedCashflowSize;
			int rowCounter = 0;

			for (BaseEntity fund : cashflows) {

				int day = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.DAY_OF_MONTH);
				currentCashflowRow++;
				Object[] cashflowRowValues = { day + "/" + currentMonth, fund.getTransactionName(),
						curr(fund.getTransactionNominal()), "" };
				createRow(xsheet, currentCashflowRow, columnOffset, cashflowRowValues);
				summaryCashflow += fund.getTransactionNominal();
				rowCounter++;
			}

			if (rowCounter < totalRowDedicatedForCurrentMonth) {
				currentCashflowRow++;
				int gap = totalRowDedicatedForCurrentMonth - rowCounter;
				for (int i = 0; i < gap; i++) {
					createRow(xsheet, currentCashflowRow, columnOffset, "", "", "", "");
					if (i < gap - 1)
						currentCashflowRow++;
				}
			}
		}

//		/**
//		 * ============ spending ==============
//		 */
//		
//		for(Integer currentMonth : comparedCashflow.keySet()) {
//			List<BaseEntity> spendings = comparedCashflow.get(currentMonth);
//			List<BaseEntity> fundInCurrentMonth = mappedCashflow.get(currentMonth);
//			
//			int fundSize = fundInCurrentMonth.size();
//			int spendingSize = spendings.size();
//			int totalRowDedicatedForCurrentMonth = fundSize > spendingSize ? fundSize : spendingSize;
//			int rowCounter = 0;
//			
//			for (BaseEntity spending : spendings) {
//				
//				int day = DateUtil.getCalendarItem(spending.getTransactionDate(), Calendar.DAY_OF_MONTH);
//				currentSpendingRow++;
//				Object[] fundRowValues = {day+"/"+currentMonth, spending.getTransactionName(), curr(spending.getTransactionNominal()), ""};
//				createRow(xsheet, currentSpendingRow, columnOffset + 4, fundRowValues );
//				summaryCashflow += spending.getTransactionNominal();
//				rowCounter++;
//			}
//			if(rowCounter < totalRowDedicatedForCurrentMonth) {
//				currentSpendingRow++;
//				int gap = totalRowDedicatedForCurrentMonth - rowCounter;
//				for(int i = 0;i<gap;i++) {
//					createRow(xsheet, currentSpendingRow, columnOffset + 4, "", "", "", ""  );
//					if(i < gap -1)
//						currentSpendingRow++;
//				}
//			}
//		}

		return summaryCashflow;
	}

}
