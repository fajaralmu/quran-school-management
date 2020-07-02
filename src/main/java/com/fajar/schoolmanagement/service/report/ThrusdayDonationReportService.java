package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.curr;
import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.dateCell;
import static com.fajar.schoolmanagement.util.DateUtil.getCalendarItem;
import static com.fajar.schoolmanagement.util.DateUtil.getDaysInOneMonth;
import static com.fajar.schoolmanagement.util.FileUtil.getFile;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.THURSDAY;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.DonationThursday;
import com.fajar.schoolmanagement.entity.FinancialEntity;
import com.fajar.schoolmanagement.report.builder.DateCell;
import com.fajar.schoolmanagement.report.builder.ExcelReportUtil;
import com.fajar.schoolmanagement.report.builder.FundAndSpendingFlowReportMonthly;
import com.fajar.schoolmanagement.service.ProgressService;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
public class ThrusdayDonationReportService {

	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ProgressService progressService;

	/////////////////////// DONATION FUND AND SPEND /////////////////////
	public File generateThrusdayCashflowReport(ReportData reportData) {
		log.info("generateThrusdayCashflowReport"); 
		
		reportData.setReportName("Mutasi_Infaq_Kamis"); 
		FundAndSpendingFlowReportMonthly reportBuilder = new FundAndSpendingFlowReportMonthly(reportData, webConfigService, progressService);
		File file = reportBuilder.buildReport();
		
		log.info("generated ThrusdayCashflowReport");
		return file;
	}

	


	/////////////// DONATION FUNDS FLOW ONLY ///////////////////////

	public File generateThrusdayDonationReport(ReportData transactionData) {
		log.info("generateThrusdayDonationReport");
		String time = ReportMappingUtil.getReportDateString();
		String sheetName = "Infaq_Kamis";

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);

		writeThrusdayDonationFundReport(xsheet, transactionData);

		File file = getFile(xwb, reportName);
		log.info("generated ThrusdayDonationReport");
		return file;
	}

	private void writeThrusdayDonationFundReport(XSSFSheet xsheet, ReportData reportData) {

		Map<Integer, List<DonationThursday>> mappedDonation = getThrusdayDonationMappedForEachMonth(reportData);
		int columnOffset = 1, currentRow = 1, maxWeek = 5;
		createReportHeader(xsheet, currentRow, columnOffset);
		currentRow++;
		int dataRow = currentRow;

		columnOffset--; // REMOVE if month starts at 0
		for (int month = 1; month <= 12; month++) {
			List<DonationThursday> donationInTheMonth = mappedDonation.get(month);
			int dataColumn = columnOffset + 2 * month;
			long summary = 0L;

			for (int week = 0; week < maxWeek; week++) {
				if (donationInTheMonth.size() > week) {
					DonationThursday summaryInTheWeek = donationInTheMonth.get(week);
					if (null != summaryInTheWeek) {
						DateCell dateCell = dateCell(summaryInTheWeek.getDate(), "dd-MM-yyyy");
						createRow(xsheet, dataRow, dataColumn, dateCell, curr(summaryInTheWeek.getNominal()));
						summary += summaryInTheWeek.getNominal();
					}
				}
				dataRow++;
			}

			createRow(xsheet, dataRow, dataColumn, curr(summary), "");

			dataRow = currentRow;
			progressService.sendProgress(1, 12, 50, reportData.getRequestId());
		}
		
		ExcelReportUtil.autosizeColumn(xsheet, new CellRangeAddress(1, 6, columnOffset, columnOffset + 25));

	}

	private void createReportHeader(XSSFSheet xsheet, int currentRow, int columnOffset) {

		// Column Label
		Object[] headerValues = getThrusdayDonationHeader();
		createRow(xsheet, currentRow, columnOffset, headerValues);
		addMergedRegionForHeaderOrTotal(xsheet, currentRow, columnOffset + 1);
		currentRow++;

		// Numbering
		int rowForTotal = currentRow + 5;
		for (int i = currentRow; i < rowForTotal; i++) {
			int number = i - currentRow + 1;
			createRow(xsheet, i, columnOffset, number);
		}
		createRow(xsheet, rowForTotal, columnOffset, "Total");
		addMergedRegionForHeaderOrTotal(xsheet, rowForTotal, columnOffset + 1);

	}

	private void addMergedRegionForHeaderOrTotal(XSSFSheet xsheet, int row, int columnOffset) {

		for (int i = 0; i < 12; i++) {
			int firstCol = i * 2 + columnOffset;
			int lastCol = firstCol + 1;
			ExcelReportUtil.addMergedRegionSingleRow(xsheet, firstCol, lastCol, row);
		}
		
	}

	private Map<Integer, List<DonationThursday>> getThrusdayDonationMappedForEachMonth(ReportData reportData) {

		int year = reportData.getFilter().getYear();
		final Map<Integer, List<FinancialEntity>> mappedFundsByMonth = ReportMappingUtil
				.sortFinancialEntityByMonth(reportData.getFunds());
		final Map<Integer, List<DonationThursday>> thursdayDonationMap = new HashMap<>();

		for (final int month : mappedFundsByMonth.keySet()) {

			int monthDays = DateUtil.getMonthsDay(month - 1, year);

			List<FinancialEntity> rawDonationThursdays = mappedFundsByMonth.get(month);
			List<DonationThursday> donationThursdays = new ArrayList<>(); // maximum item: 5
			Map<Integer, List<FinancialEntity>> mappedDonationsByDay = ReportMappingUtil
					.sortFinancialEntityByDayOfMonth(rawDonationThursdays, monthDays);

			Date[] thrusdaysInCurrentMonth = getDaysInOneMonth(THURSDAY, month - 1, year);
			int dayOfMonth = 1;

			for (Date thursday : thrusdaysInCurrentMonth) {
				if (null == thursday)
					continue;

				int currentDayOfMonth = getCalendarItem(thursday, DAY_OF_MONTH);
				long transactionNominal = 0L;

				loop: for (int day = dayOfMonth; day <= currentDayOfMonth; day++) {
					List<FinancialEntity> donationInTheDay = mappedDonationsByDay.get(day);
					if (null == donationInTheDay) {
						continue loop;
					}
					for (FinancialEntity donation : donationInTheDay) {
						transactionNominal += donation.getTransactionNominal();
					}
				}

				donationThursdays.add(generateThursdayDonationSummary(transactionNominal, thursday));

				dayOfMonth = currentDayOfMonth + 1;

			}
			thursdayDonationMap.put(month, donationThursdays);
			
			progressService.sendProgress(1, mappedFundsByMonth.keySet().size(), 30, reportData.getRequestId());
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
		Object[] values = new Object[25];
		values[0] = "Minggu Ke";
		String[] monthNames = DateUtil.MONTH_NAMES;
		int index = 1;
		for (int i = 0; i < monthNames.length; i++) {
			values[index] = monthNames[i];
			index++;
			values[index] = "";
			index++;
		}

		return values;
	}

}
