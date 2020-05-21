package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.curr;
import static com.fajar.schoolmanagement.util.DateUtil.MONTH_NAMES;
import static com.fajar.schoolmanagement.util.DateUtil.getCalendarItem;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CashBalance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CashflowReportMonthlySeparated {

	final ReportData reportData;
	final XSSFSheet xsheet;
	final Map<Integer, List<BaseEntity>> mappedFunds;
	final Map<Integer, List<BaseEntity>> mappedSpendings;

	public CashflowReportMonthlySeparated(ReportData reportData, XSSFSheet sheet) {
		this.reportData = reportData;
		this.xsheet = sheet;
		mappedFunds = ReportMappingUtil.sortFinancialEntityByMonth(reportData.getFunds());
		mappedSpendings = ReportMappingUtil.sortFinancialEntityByMonth(reportData.getSpendings());
	}

	public void writeReport() {

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
	}

	private void writeInitialBalance(int currentRow, int columnOffset) {
		CashBalance initialBalance = reportData.getInitialBalance();
		Object[] initialBalanceRow = { "1/1", "Saldo Awal", "", curr(initialBalance.getActualBalance()), "", "", "",
				"" };
		createRow(xsheet, currentRow, columnOffset, initialBalanceRow);
	}

	private long getSummary(List<BaseEntity> cashflow) {
		long summary = 0L;
		for (BaseEntity baseEntity : cashflow) {
			summary += baseEntity.getTransactionNominal();
		}
		return summary;
	}

	public int writeMonthlyCashflowTable(final int currentRow, Map<Integer, List<BaseEntity>> mappedCashflow,
			Map<Integer, List<BaseEntity>> comparedCashflow, int columnOffset) {

		int currentCashflowRow = currentRow;
		int totalRow = 0;

		for (Integer currentMonth : mappedCashflow.keySet()) {
			currentCashflowRow++;
			createRow(xsheet, currentCashflowRow, columnOffset, MONTH_NAMES[currentMonth - 1], "", "", "");

			List<BaseEntity> cashflows = mappedCashflow.get(currentMonth);
			List<BaseEntity> comparedCashflowInCurrentMonth = comparedCashflow.get(currentMonth);

			int cashflowSize = cashflows.size();
			int comparedCashflowSize = comparedCashflowInCurrentMonth.size();
			int totalRowDedicatedForCurrentMonth = cashflowSize > comparedCashflowSize ? cashflowSize
					: comparedCashflowSize;
			int rowCounter = 0;

			for (BaseEntity cashflow : cashflows) {
 
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

	private void writeCashflowRow(BaseEntity fund, int currentCashflowRow, int columnOffset) {
		
		int day = getCalendarItem(fund.getTransactionDate(), DAY_OF_MONTH);
		int month = getCalendarItem(fund.getTransactionDate(), Calendar.MONTH )+1;
		log.info("fund.getTransactionDate(): {}, month: {}", fund.getTransactionDate(), month);
		Object[] cashflowRowValues = { day + "/" + month, fund.getTransactionName(),
				curr(fund.getTransactionNominal()), "" };
		createRow(xsheet, currentCashflowRow, columnOffset, cashflowRowValues);
	}
	
	 

}
