package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.addMergedRegionSingleColumn;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.addMergedRegionSingleRow;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.curr;
import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.dateCell;
import static com.fajar.schoolmanagement.util.FileUtil.getFile;

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

import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudentDonationReportService {

	@Autowired
	private WebConfigService webConfigService;

	public File generateMonthlyStudentDonationReport(ReportData reportData, List<Student> students) {

		Filter filter = reportData.getFilter();
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Infaq_Bulanan_Santri-" + filter.getMonth() + "-" + filter.getYear();

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);
		writeMonthlyStudentDonationReport(xsheet, reportData, students);

		File file = getFile(xwb, reportName);
		return file;

	}
	
	private Map<Long, List<DonationMonthly>> mapStudentMonthlyDonation(List<BaseEntity> studentDonations){
		Map<Long, List<DonationMonthly>> mappedFunds = new HashMap<>();
		for (BaseEntity baseEntity : studentDonations) {
			DonationMonthly donation = (DonationMonthly) baseEntity;
			long studentId = donation.getStudent().getId();
			
			validateMapValueIfNull(mappedFunds, studentId, new ArrayList<>());
			
			mappedFunds.get(studentId).add(donation);
			
		}
		
		return mappedFunds;
	}
	
	private void validateMapValueIfNull(Map theMap, long mapKey, Object deafultValue) {
		
		if(theMap.get(mapKey) == null) {
			theMap.put(mapKey, deafultValue);
		}
	}

	private void writeMonthlyStudentDonationReport(XSSFSheet sheet, ReportData reportData, List<Student> students) {

		List<BaseEntity> funds = reportData.getFunds();
		Map<Long, List<DonationMonthly>> mappedFunds = mapStudentMonthlyDonation(funds);
		Object[] headerValues = getStudentDonationHeader();
		Object[] headerValuesSecondRow = getStudentDonationHeaderSecondRow();

		int columnOffset = 1;
		int currentRow = 1;

		addMergedRegionForHeader(sheet, currentRow, columnOffset);
		createRow(sheet, currentRow, columnOffset, headerValues);
		currentRow++;

		createRow(sheet, currentRow, columnOffset, headerValuesSecondRow);
		currentRow++;

		for (int i = 0; i < students.size(); i++) {
			Student student = students.get(i);
			List<DonationMonthly> studentDonation = mappedFunds.get(student.getId());

			Object[] studentRowData = writeStudentDonationList(student, studentDonation, i + 1);

			createRow(sheet, currentRow, columnOffset, studentRowData);
			currentRow++;
		}

	}

	private void addMergedRegionForHeader(XSSFSheet sheet, int currentRow, int columnOffset) {

		addMergedRegionSingleColumn(sheet, currentRow, currentRow + 1, columnOffset);
		addMergedRegionSingleColumn(sheet, currentRow, currentRow + 1, columnOffset + 1);
		for (int i = 0; i < 12; i++) {
			int offset = columnOffset + 2;
			addMergedRegionSingleRow(sheet, offset + i * 2, offset + i * 2 + 1, currentRow);
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

			log.info("map mappedStudentDonation month: {}, transactionDate: {}", month, transactionDate);
			Object donationDate = "";
			Object donationValue = "";

			if (null != transactionDate) {
				int tranMonth = DateUtil.getCalendarItem(transactionDate, Calendar.MONTH) + 1;
				int tranDay = DateUtil.getCalendarItem(transactionDate, Calendar.DAY_OF_MONTH);

				donationDate = dateCell(transactionDate, "dd-MM-yyyy");
				donationValue = curr(studentDonation.getNominal());
			}
			rowData[index] = donationDate;
			index++;
			rowData[index] = donationValue;
			index++;
		}

		return rowData;
	}

	private Map fillMapWithMonthKeys(Object dafaultValue) {
		Map map = new HashMap<>();
		for (int i = 1; i <= 12; i++) {
			map.put(i, dafaultValue);
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