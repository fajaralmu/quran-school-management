package com.fajar.schoolmanagement.service.report;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.report.builder.StudentDonationReportBuilder;
import com.fajar.schoolmanagement.service.WebConfigService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudentDonationReportService {

	@Autowired
	private WebConfigService webConfigService;

	public File generateMonthlyStudentDonationForOneYearReport(ReportData reportData, List<Student> students) {
		StudentDonationReportBuilder reportBuilder = new StudentDonationReportBuilder(webConfigService, reportData, students); 
		
		File file = reportBuilder.buildReport();
		log.info("generateMonthlyStudentDonationReport success..");
		return file;

	}
	
	/*
	 * private Map<Long, List<DonationMonthly>>
	 * mapStudentMonthlyDonation(List<FinancialEntity> studentDonations){ Map<Long,
	 * List<DonationMonthly>> mappedFunds = new HashMap<>(); for (FinancialEntity
	 * baseEntity : studentDonations) { DonationMonthly donation = (DonationMonthly)
	 * baseEntity; long studentId = donation.getStudent().getId();
	 * 
	 * validateMapValueIfNull(mappedFunds, studentId, new ArrayList<>());
	 * 
	 * mappedFunds.get(studentId).add(donation);
	 * 
	 * }
	 * 
	 * return mappedFunds; }
	 * 
	 * private <K, V> void validateMapValueIfNull(Map<K, V> theMap, K mapKey, V
	 * deafultValue) {
	 * 
	 * if(theMap.get(mapKey) == null) { theMap.put(mapKey, deafultValue); } }
	 * 
	 * private void writeMonthlyStudentDonationReport(XSSFSheet sheet, ReportData
	 * reportData, List<Student> students) {
	 * 
	 * List<FinancialEntity> funds = reportData.getFunds(); Map<Long,
	 * List<DonationMonthly>> mappedFunds = mapStudentMonthlyDonation(funds); int
	 * columnOffset = 1; int currentRow = 1;
	 * 
	 * addMergedRegionForHeader(sheet, currentRow, columnOffset);
	 * createStudentDonationReportHeader(sheet, currentRow, columnOffset);
	 * currentRow+=2;
	 * 
	 * for (int i = 0; i < students.size(); i++) { Student student =
	 * students.get(i); List<DonationMonthly> studentDonation =
	 * mappedFunds.get(student.getId());
	 * 
	 * Object[] studentRowData = writeStudentDonationList(student, studentDonation,
	 * i + 1);
	 * 
	 * createRow(sheet, currentRow, columnOffset, studentRowData); currentRow++; }
	 * 
	 * }
	 * 
	 * private void createStudentDonationReportHeader(XSSFSheet sheet, int
	 * currentRow, int columnOffset) {
	 * 
	 * createFirstHeader(sheet, currentRow, columnOffset); currentRow++;
	 * createSecorndHeader(sheet, currentRow, columnOffset); }
	 * 
	 * private void createSecorndHeader(XSSFSheet sheet, int currentRow, int
	 * columnOffset) {
	 * 
	 * Object[] headerValuesSecondRow = getStudentDonationHeaderSecondRow();
	 * createRow(sheet, currentRow, columnOffset, headerValuesSecondRow); }
	 * 
	 * private void createFirstHeader(XSSFSheet sheet, int currentRow, int
	 * columnOffset) {
	 * 
	 * Object[] headerValues = getStudentDonationHeader(); createRow(sheet,
	 * currentRow, columnOffset, headerValues); }
	 * 
	 * private void addMergedRegionForHeader(XSSFSheet sheet, int currentRow, int
	 * columnOffset) {
	 * 
	 * addMergedRegionSingleColumn(sheet, currentRow, currentRow + 1, columnOffset);
	 * addMergedRegionSingleColumn(sheet, currentRow, currentRow + 1, columnOffset +
	 * 1); for (int i = 0; i < 12; i++) { int offset = columnOffset + 2;
	 * addMergedRegionSingleRow(sheet, offset + i * 2, offset + i * 2 + 1,
	 * currentRow); } }
	 * 
	 * private Object[] writeStudentDonationList(Student student,
	 * List<DonationMonthly> studentDonations, int number) {
	 * 
	 * String studentName = student.getFullName();
	 * 
	 * Object[] rowData = new Object[26]; rowData[0] = number; rowData[1] =
	 * studentName; int index = 2;
	 * 
	 * if (null == studentDonations) { studentDonations = new ArrayList<>(); }
	 * 
	 * Map<Integer, DonationMonthly> mappedStudentDonation =
	 * mapStudentDonationByMonth(studentDonations);
	 * 
	 * for (int month : mappedStudentDonation.keySet()) {
	 * 
	 * DonationMonthly studentDonation = mappedStudentDonation.get(month); Date
	 * transactionDate = studentDonation.getTransactionDate();
	 * 
	 * Object donationDate = ""; Object donationValue = "";
	 * 
	 * if (null != transactionDate) {
	 * 
	 * donationDate = dateCell(transactionDate, "dd-MM-yyyy"); donationValue =
	 * curr(studentDonation.getNominal()); } rowData[index] = donationDate; index++;
	 * rowData[index] = donationValue; index++; }
	 * 
	 * return rowData; }
	 * 
	 * private <K, V> Map<K, V> fillMapWithMonthKeys(V dafaultValue) { Map<K, V> map
	 * = new HashMap<K, V>(); for (Integer i = 1; i <= 12; i++) { map.put((K)i,
	 * dafaultValue); }
	 * 
	 * return map; }
	 * 
	 * private Object[] getStudentDonationHeader() { Object[] values = new
	 * Object[26]; values[0] = "No"; values[1] = "Nama"; String[] monthNames =
	 * DateUtil.MONTH_NAMES; int index = 2; for (int i = 0; i < monthNames.length;
	 * i++) { values[index] = monthNames[i]; index++; values[index] = ""; index++; }
	 * 
	 * return values; }
	 * 
	 * private Object[] getStudentDonationHeaderSecondRow() { Object[] values = new
	 * Object[26]; values[0] = ""; values[1] = ""; String[] monthNames =
	 * DateUtil.MONTH_NAMES; int index = 2; for (int i = 0; i < monthNames.length;
	 * i++) { values[index] = "Tanggal"; index++; values[index] = "Rp"; index++; }
	 * 
	 * return values; }
	 * 
	 * private Map<Integer, DonationMonthly>
	 * mapStudentDonationByMonth(List<DonationMonthly> studentDonations) {
	 * 
	 * Map<Integer, DonationMonthly> mappedData = fillMapWithMonthKeys(new
	 * DonationMonthly());
	 * 
	 * for (DonationMonthly donationMonthly : studentDonations) { int month =
	 * donationMonthly.getMonth(); mappedData.put(month, donationMonthly); }
	 * 
	 * return mappedData; }
	 */
}
