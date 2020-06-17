package com.fajar.schoolmanagement.report.builder;

import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.addMergedRegionSingleColumn;
import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.addMergedRegionSingleRow;
import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.curr;
import static com.fajar.schoolmanagement.report.builder.ExcelReportUtil.dateCell;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.FinancialEntity;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.service.report.ReportMappingUtil;
import com.fajar.schoolmanagement.util.DateUtil;
import com.fajar.schoolmanagement.util.MyFileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StudentDonationReportBuilder extends ReportBuilder {

	private static final String DATE_PATTERN_STD = "dd-MM-yyyy";
	final List<Student> students;

	public StudentDonationReportBuilder(WebConfigService configService, ReportData reportData, List<Student> students) {
		super(configService, reportData);
		
		this.students = students;
	}

	@Override
	public File buildReport() {
		log.info("generateMonthlyStudentDonationReport");

		writeMonthlyStudentDonationReport();

		File file = MyFileUtil.getFile(xssfWorkbook, reportName);
		log.info("generated: MonthlyStudentDonationReport");

		return file;
	}

	@Override
	public void init() {

		Filter filter = reportData.getFilter();
		String time = ReportMappingUtil.getReportDateString();
		String sheetName = "Infaq_Bulanan_Santri-" + filter.getMonth() + "-" + filter.getYear();

		reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		xssfWorkbook = new XSSFWorkbook();
		xsheet = xssfWorkbook.createSheet(sheetName);
	}

	private Map<Long, List<DonationMonthly>> mapStudentMonthlyDonation(List<FinancialEntity> studentDonations) {
		Map<Long, List<DonationMonthly>> mappedFunds = new HashMap<>();
		for (FinancialEntity baseEntity : studentDonations) {
			DonationMonthly donation = (DonationMonthly) baseEntity;
			long studentId = donation.getStudent().getId();

			validateMapValueIfNull(mappedFunds, studentId, new ArrayList<>());

			mappedFunds.get(studentId).add(donation);

		}

		return mappedFunds;
	}

	private <K, V> void validateMapValueIfNull(Map<K, V> theMap, K mapKey, V deafultValue) {

		if (theMap.get(mapKey) == null) {
			theMap.put(mapKey, deafultValue);
		}
	}

	private void writeMonthlyStudentDonationReport() {

		List<FinancialEntity> funds = reportData.getFunds();
		Map<Long, List<DonationMonthly>> mappedFunds = mapStudentMonthlyDonation(funds);
		int columnOffset = 1;
		int currentRow = 1;

		addMergedRegionForHeader(currentRow, columnOffset);
		createStudentDonationReportHeader(currentRow, columnOffset);
		currentRow += 2;

		for (int i = 0; i < students.size(); i++) {
			Student student = students.get(i);
			List<DonationMonthly> studentDonation = mappedFunds.get(student.getId());

			Object[] studentRowData = writeStudentDonationList(student, studentDonation, i + 1);

			createRow(xsheet, currentRow, columnOffset, studentRowData);
			currentRow++;
		}

	}

	private void createStudentDonationReportHeader(int currentRow, int columnOffset) {

		createFirstHeader(currentRow, columnOffset);
		currentRow++;
		createSecorndHeader(currentRow, columnOffset);
	}

	private void createSecorndHeader(int currentRow, int columnOffset) {

		Object[] headerValuesSecondRow = getStudentDonationHeaderSecondRow();
		createRow(xsheet, currentRow, columnOffset, headerValuesSecondRow);
	}

	private void createFirstHeader(int currentRow, int columnOffset) {

		Object[] headerValues = getStudentDonationHeader();
		createRow(xsheet, currentRow, columnOffset, headerValues);
	}

	private void addMergedRegionForHeader(int currentRow, int columnOffset) {

		addMergedRegionSingleColumn(xsheet, currentRow, currentRow + 1, columnOffset);
		addMergedRegionSingleColumn(xsheet, currentRow, currentRow + 1, columnOffset + 1);
		for (int i = 0; i < 12; i++) {
			int offset = columnOffset + 2;
			addMergedRegionSingleRow(xsheet, offset + i * 2, offset + i * 2 + 1, currentRow);
		}
	}

	private Object[] writeStudentDonationList(Student student, List<DonationMonthly> studentDonations, int number) {

		String studentName = student.getFullName();

		Object[] rowData = new Object[26];
		rowData[0] = number;
		rowData[1] = studentName;
		int index = 2;

		if (null == studentDonations) {
			studentDonations = new ArrayList<>();
		}

		Map<Integer, DonationMonthly> mappedStudentDonation = mapStudentDonationByMonth(studentDonations);

		for (int month : mappedStudentDonation.keySet()) {

			DonationMonthly studentDonation = mappedStudentDonation.get(month);
			Date transactionDate = studentDonation.getTransactionDate();

			Object donationDate = "";
			Object donationValue = "";

			if (null != transactionDate) {

				donationDate = dateCell(transactionDate, DATE_PATTERN_STD);
				donationValue = curr(studentDonation.getNominal());
			}
			rowData[index] = donationDate;
			index++;
			rowData[index] = donationValue;
			index++;
		}

		return rowData;
	}

	private <K, V> Map<K, V> fillMapWithMonthKeys(V dafaultValue) {
		Map<K, V> map = new HashMap<K, V>();
		for (Integer i = 1; i <= 12; i++) {
			map.put((K) i, dafaultValue);
		}

		return map;
	}

	private Object[] getStudentDonationHeader() {
		Object[] values = new Object[26];
		values[0] = "No";
		values[1] = "Nama";
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

	private Object[] getStudentDonationHeaderSecondRow() {
		Object[] values = new Object[26];
		values[0] = "";
		values[1] = "";
		String[] monthNames = DateUtil.MONTH_NAMES;
		int index = 2;
		for (int i = 0; i < monthNames.length; i++) {
			values[index] = "Tanggal";
			index++;
			values[index] = "Rp";
			index++;
		}

		return values;
	}

	private Map<Integer, DonationMonthly> mapStudentDonationByMonth(List<DonationMonthly> studentDonations) {

		Map<Integer, DonationMonthly> mappedData = fillMapWithMonthKeys(new DonationMonthly());

		for (DonationMonthly donationMonthly : studentDonations) {
			int month = donationMonthly.getMonth();
			mappedData.put(month, donationMonthly);
		}

		return mappedData;
	}
}
