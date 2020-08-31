package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.curr;
import static com.fajar.schoolmanagement.service.report.ReportMappingUtil.getCashflowItemCount;
import static com.fajar.schoolmanagement.service.report.ReportMappingUtil.getMonthDays;
import static com.fajar.schoolmanagement.service.report.ReportMappingUtil.sortFinancialEntityByDayOfMonth;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.FinancialEntity;
import com.fajar.schoolmanagement.service.ProgressService;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CashflowReportService {

	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ProgressService progressService;
	
	private ReportData reportData;

	private String reportPath;

	@PostConstruct
	public void init() {
		this.reportPath = webConfigService.getReportPath();
	}

	public XSSFWorkbook generateMonthlyGeneralCashflow(ReportData reportData) {

		Filter filter = reportData.getFilter();
		this.reportData = reportData;
		String time = ReportMappingUtil.getReportDateString();
		String sheetName = "Laporan_Bulanan-" + filter.getMonth() + "-" + filter.getYear();

		String reportName = reportPath + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);
		writeMonthlyGeneralCashflow(xsheet, reportData);

//		File file = getFile(xwb, reportName);
		return xwb;
	}

	private void writeMonthlyGeneralCashflow(XSSFSheet xsheet, ReportData reportData) {

		Filter filter = reportData.getFilter();
		Integer monthDays = getMonthDays(filter);
		int month = filter.getMonth();

		Map<Integer, List<FinancialEntity>> mappedFunds = sortFinancialEntityByDayOfMonth(reportData.getFunds(),
				monthDays);
		Map<Integer, List<FinancialEntity>> mappedSpendings = sortFinancialEntityByDayOfMonth(reportData.getSpendings(),
				monthDays);
		CashBalance initialBalance = reportData.getInitialBalance();

		progressService.sendProgress(1, 1, 20, reportData.getRequestId());
		
		int currentRow = 1;
		int columnOffset = 1;

		/**
		 * build fund table
		 */
		writeHeaderValuesForMonthlyCashflow(xsheet, currentRow, columnOffset);

		// initial balance row
		int fundRow = currentRow++;
		int spendingRow = fundRow;

		// funds
		fundRow++;
		initialBalance.setDate(DateUtil.getDate(reportData.getFilter().getYear(), month, 1));
		writeInitialBalance(xsheet, fundRow, columnOffset, initialBalance);
		long summaryFund = writeMonthlyCashflowTable(fundRow, mappedFunds, xsheet, columnOffset);
		int fundRowCount = 1 + getCashflowItemCount(mappedFunds); // one for intiial Balance

		// spending
		long summarySpending = writeMonthlyCashflowTable(spendingRow, mappedSpendings, xsheet, 5);
		int spendingRowCount = getCashflowItemCount(mappedSpendings);

		int rowForTotal = fundRowCount > spendingRowCount ? fundRowCount + 2 : spendingRowCount + 2;

		// rowTotal
		long grandTotalFund = summaryFund + initialBalance.getActualBalance();
		long grandTotalBalance = grandTotalFund - summarySpending;
		createRow(xsheet, rowForTotal, 1, "", "", curr(summaryFund), curr(grandTotalFund), "", "",
				curr(summarySpending), curr(grandTotalBalance));

		log.info("Spending Row: {}", spendingRowCount);
		log.info("Fund Row: {}", fundRowCount);
		log.info("Total Fund: {}, initialBalance: {}", summaryFund, initialBalance.getActualBalance());
	}

	private void writeInitialBalance(XSSFSheet xsheet, int fundRow, int columnOffset, CashBalance initialBalance) {
		int month = DateUtil.getCalendarItem(initialBalance.getDate(), Calendar.MONTH) + 1;
		Object[] initialBalanceRow = { "1/" + month, "Saldo Awal", "", curr(initialBalance.getActualBalance()) };
		createRow(xsheet, fundRow, columnOffset, initialBalanceRow);
	}

	private void writeHeaderValuesForMonthlyCashflow(XSSFSheet xsheet, int currentRow, int columnOffset) {

		Object[] headerValues = { "Tanggal", "Keterangan", "Debet", "Total", "Tanggal", "Keterangan", "Kredit",
				"Total" };
		createRow(xsheet, currentRow, columnOffset, headerValues);
	}

	private long writeMonthlyCashflowTable(int currentRow, Map<Integer, List<FinancialEntity>> mappedCashflow,
			XSSFSheet xsheet, int columnOffset) {
		long summaryCashflow = 0L;
		for (Integer day : mappedCashflow.keySet()) {
			List<FinancialEntity> funds = mappedCashflow.get(day);
			for (FinancialEntity fund : funds) {

				int month = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.MONTH) + 1;
				currentRow++;
				Object[] fundRowValues = { day + "/" + month, fund.getTransactionName(),
						curr(fund.getTransactionNominal()), "" };
				createRow(xsheet, currentRow, columnOffset, fundRowValues);
				summaryCashflow += fund.getTransactionNominal(); 
				
			}
			progressService.sendProgress(1,  mappedCashflow.keySet().size(), 30, reportData.getRequestId());
		}

		return summaryCashflow;
	}

}
