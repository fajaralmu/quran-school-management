package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.curr;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.dateCell;
import static com.fajar.schoolmanagement.util.DateUtil.MONTH_NAMES;
import static com.fajar.schoolmanagement.util.DateUtil.getCalendarItem;
import static com.fajar.schoolmanagement.util.DateUtil.getDaysInOneMonth;
import static com.fajar.schoolmanagement.util.FileUtil.getFile;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.THURSDAY;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.DonationThursday;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class ThrusdayDonationReportService {

	@Autowired
	private WebConfigService webConfigService;

	public File generateThrusdayCashflowReport(ReportData transactionData) {
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Mutasi_Infaq_Kamis";

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
		currentRow++;

		// funds
		writeInitialBalance(xsheet, currentRow, columnOffset, initialBalance);
		final int fundRowCount = writeMonthlyCashflowTable(currentRow, mappedFunds, mappedSpendings, xsheet,
				columnOffset);
		final long summaryFund = getSummary(reportData.getFunds()); // one for intiial Balance

		// spending
		final int spendingRowCount = writeMonthlyCashflowTable(currentRow, mappedSpendings, mappedFunds, xsheet, 5);
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

	private void writeInitialBalance(XSSFSheet xsheet, int currentRow, int columnOffset, CashBalance initialBalance) {

		Object[] initialBalanceRow = { "1/1", "Saldo Awal", "", curr(initialBalance.getActualBalance()) };
		createRow(xsheet, currentRow, columnOffset, initialBalanceRow);
	}

	private long getSummary(List<BaseEntity> cashflow) {
		long summary = 0L;
		for (BaseEntity baseEntity : cashflow) {
			summary += baseEntity.getTransactionNominal();
		}
		return summary;
	}

	private static int writeMonthlyCashflowTable(final int currentRow, Map<Integer, List<BaseEntity>> mappedCashflow,
			Map<Integer, List<BaseEntity>> comparedCashflow, XSSFSheet xsheet, int columnOffset) {

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

			for (BaseEntity fund : cashflows) {

				int day = getCalendarItem(fund.getTransactionDate(), DAY_OF_MONTH);
				currentCashflowRow++;
				Object[] cashflowRowValues = { day + "/" + currentMonth, fund.getTransactionName(),
						curr(fund.getTransactionNominal()), "" };
				createRow(xsheet, currentCashflowRow, columnOffset, cashflowRowValues);

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

	public static void main(String[] a) {
		Date[] dates = getDaysInOneMonth(Calendar.SATURDAY, 8, 1996);
		for (Date date : dates) {
			System.out.println("date: " + date);
		}
	}

	// ================================================ FUNDS FLOW ONLY

	public File generateThrusdayDonationReport(ReportData transactionData) {
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Infaq_Kamis";

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);

		writeThrusdayDonationReport(xsheet, transactionData);

		File file = getFile(xwb, reportName);
		return file;
	}

	private void writeThrusdayDonationReport(XSSFSheet xsheet, ReportData reportData) {

		Map<Integer, List<DonationThursday>> mappedDonation = getThrusdayDonationMappedForEachMonth(reportData);
		int columnOffset = 1;
		int currentRow = 1;
		int maxWeek = 5;
		createReportHeader(xsheet, currentRow, columnOffset);
		currentRow++;
		int dataRow = currentRow;

		for (int month = 1; month <= 12; month++) {
			List<DonationThursday> donationInTheMonth = mappedDonation.get(month);
			int dataColumn = columnOffset + 2 * month;
			long summary = 0L;

			for (int week = 0; week < maxWeek; week++) {
				DonationThursday summaryInTheWeek = donationInTheMonth.get(week);
				if (null != summaryInTheWeek) {
					DateCell dateCell = dateCell(summaryInTheWeek.getDate(), "dd-MM-yyyy");
					createRow(xsheet, dataRow, dataColumn, dateCell, curr(summaryInTheWeek.getNominal()));
					summary += summaryInTheWeek.getNominal();
				}
				dataRow++;
			}

			createRow(xsheet, dataRow, dataColumn, curr(summary));

			dataRow = currentRow;
		}

	}

	private void createReportHeader(XSSFSheet xsheet, int currentRow, int columnOffset) {

		Object[] headerValues = getThrusdayDonationHeader();
		createRow(xsheet, currentRow, columnOffset, headerValues);
		currentRow++;
		// Numbering
		for (int i = currentRow; i <= currentRow + 5; i++) {
			createRow(xsheet, i, columnOffset, i);
		}
	}

	private Map<Integer, List<DonationThursday>> getThrusdayDonationMappedForEachMonth(ReportData reportData) {

		int year = reportData.getFilter().getYear();
		final Map<Integer, List<BaseEntity>> mappedFundsByMonth = CashflowReportService
				.sortFinancialEntityByMonth(reportData.getFunds());
		final Map<Integer, List<DonationThursday>> thursdayDonationMap = new HashMap<>();

		for (int month : mappedFundsByMonth.keySet()) {

			int monthDays = DateUtil.getMonthsDay(month, year);

			List<BaseEntity> rawDonationThursdays = mappedFundsByMonth.get(month);
			List<DonationThursday> donationThursdays = new ArrayList<>(); // maximum item: 5
			Map<Integer, List<BaseEntity>> mappedDonationsByDay = CashflowReportService
					.sortFinancialEntityByDayOfMonth(rawDonationThursdays, monthDays);

			Date[] thrusdaysInCurrentMonth = getDaysInOneMonth(THURSDAY, month, year);
			int dayOfMonth = 1;

			for (Date thursday : thrusdaysInCurrentMonth) {

				int currentDayOfMonth = getCalendarItem(thursday, DAY_OF_MONTH);
				long transactionNominal = 0L;

				for (int day = dayOfMonth; day <= currentDayOfMonth; day++) {
					List<BaseEntity> donationInTheDay = mappedDonationsByDay.get(day);
					for (BaseEntity donation : donationInTheDay) {
						transactionNominal += donation.getTransactionNominal();
					}
				}

				donationThursdays.add(generateThursdayDonationSummary(transactionNominal, thursday));

				dayOfMonth = currentDayOfMonth + 1;

			}
			thursdayDonationMap.put(month, donationThursdays);

		}

		return thursdayDonationMap;
	}

	private DonationThursday generateThursdayDonationSummary(long transactionNominal, Date thursday) {
		DonationThursday donationThursdaySummary = new DonationThursday();
		donationThursdaySummary.setDate(thursday);
		donationThursdaySummary.setNominal(transactionNominal);
		return donationThursdaySummary;
	}

	private Object[] getThrusdayDonationHeader() {
		Object[] values = new Object[26];
		values[0] = "Minggu Ke";
		values[1] = "Tanggal";
		String[] monthNames = DateUtil.MONTH_NAMES;
		int index = 2;
		for (int i = 0; i < monthNames.length; i++) {
			values[index] = monthNames[i];
			index++;
			values[index] = "";
			index++;
		}

		return values;
	}

}
