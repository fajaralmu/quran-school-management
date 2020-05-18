package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.util.ExcelReportUtil.createRow;
import static com.fajar.schoolmanagement.util.FileUtil.getFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.CurrencyCell;
import com.fajar.schoolmanagement.util.DateUtil;
import com.fajar.schoolmanagement.util.ExcelReportUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportBuilderService {
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

		int rowForTotal = fundRowCount > spendingRowCount ? fundRowCount : spendingRowCount;
		
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
		Map<Integer, List<BaseEntity>> mappedFunds = fillMapKeysWithDays(monthDays);

		for (int i = 0; i < funds.size(); i++) {
			BaseEntity fund = funds.get(i); 
			
			int transactionDay = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.DAY_OF_MONTH); 
			mappedFunds.get(transactionDay).add(fund);
		}
		
		return mappedFunds;
	}
	 

	private Map<Integer, List<BaseEntity>> fillMapKeysWithDays(int dayCount) {
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
	
	public static CurrencyCell curr(long value) {
		return new CurrencyCell(value);
	}

	public File generateYearlyStudentMonthlyDonationReport(ReportData reportData, List<Student> students) {
		 
		Filter filter = reportData.getFilter();
		String time = DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
		String sheetName = "Infaq_Bulanan_Santri-" + filter.getMonth() + "-" + filter.getYear();

		String reportName = reportPath + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);
		writeMonthlyDonationReport(xsheet, reportData, students);

		File file = getFile(xwb, reportName);
		return file;
		 
	}
	
	private void writeMonthlyDonationReport(XSSFSheet sheet, ReportData reportData, List<Student> students) {
		
		List<BaseEntity> funds = reportData.getFunds();
		Map<Long, List<DonationMonthly>> mappedFunds = mapStudentMonthlyDonation(funds);
		Object[] headerValues = getStudentDonationHeader();
		Object[] headerValuesSecondRow = getStudentDonationHeaderSecondRow();
		
		int columnOffset = 1;
		int currentRow = 1; 
		
		//merge No Cell
		ExcelReportUtil.addMergedRegionColumnOnly(sheet, currentRow, currentRow+1, columnOffset); 
		//merge Name Cell
		ExcelReportUtil.addMergedRegionColumnOnly(sheet, currentRow, currentRow+1, columnOffset + 1 );
		for(int i = 0; i< 12; i++) {
			int offset = columnOffset + 2 ;
			ExcelReportUtil.addMergedRegionRownOnly(sheet, offset + i * 2, offset + i * 2 + 1, currentRow );
		}
		createRow(sheet, currentRow, columnOffset, headerValues);
		currentRow++;
						
		createRow(sheet, currentRow, columnOffset, headerValuesSecondRow);
		currentRow++;
		
		for (int i = 0; i < students.size(); i++) {
			Student student = students.get(i);
			List<DonationMonthly> studentDonation = mappedFunds.get(student.getId());
			
			Object[] studentRowData;
			if(null != studentDonation && studentDonation.size() > 0) {
				studentRowData = writeStudentDonationList(studentDonation, i+1);
			}else {
				studentRowData = new Object[]{ i+1,  student.getFullName() };
			}
			
			createRow(sheet, currentRow, columnOffset, studentRowData);
			currentRow++;
		}
		 
	}

	private Object[] writeStudentDonationList(List<DonationMonthly> studentDonations, int number) {
		
		String studentName = studentDonations.get(0).getStudent().getFullName();
		
		Object[] rowData = new Object[26]; 
		rowData[0] = number;
		rowData[1] = studentName;
		int index = 2;
		
		Map<Integer, DonationMonthly> mappedStudentDonation = mapStudentDonationByMonth(studentDonations);
		
		for(int month : mappedStudentDonation.keySet()) {
			
			DonationMonthly studentDonation = mappedStudentDonation.get(month);
			Date transactionDate = studentDonation.getTransactionDate();
			
			log.info("map mappedStudentDonation month: {}, transactionDate: {}", month, transactionDate);
			if(null == transactionDate) {
				index+=2;
				continue;
			}
			
			int tranMonth = DateUtil.getCalendarItem(transactionDate, Calendar.MONTH) + 1;
			int tranDay = DateUtil.getCalendarItem(transactionDate, Calendar.DAY_OF_MONTH);
			
			rowData[index] = tranDay+"/"+tranMonth;
			index++;
			rowData[index] = curr(studentDonation.getNominal());
			index++;
		}
		
		return rowData;
	}
	
	private Map<Integer, DonationMonthly> mapStudentDonationByMonth(List<DonationMonthly> studentDonations){
		
		Map<Integer, DonationMonthly> mappedData = fillMapWithMonthKeys(new DonationMonthly());
		
		for (DonationMonthly donationMonthly : studentDonations) {
			int month = donationMonthly.getMonth();
			mappedData.put(month, donationMonthly);
		}
		
		return mappedData ;
	}

	private Map  fillMapWithMonthKeys(Object dafaultValue) {
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
		
		return values ;
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
		
		return values ;
	}

	public Map<Long, List<DonationMonthly>> mapStudentMonthlyDonation(List<BaseEntity> studentDonations){
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

}
