package com.fajar.schoolmanagement.service.transaction;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.FinancialEntity;
import com.fajar.schoolmanagement.repository.CashBalanceRepository;
import com.fajar.schoolmanagement.repository.DonationOrphanRepository;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class CashBalanceService {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	@Autowired
	private CashBalanceRepository cashBalanceRepository; 
	@Autowired
	private DonationOrphanRepository donationOrphanRepository;
	@Autowired
	private EntityRepository entityRepository;
	
	public CashBalance getBalanceByTransactionItem(FinancialEntity baseEntity) {
		
		log.info("getBalanceByTransactionItem: {} {}", baseEntity.getId(), baseEntity.getClass());
		
		CashBalance journalInfo = mapCashBalance(baseEntity);
		CashBalance balance = cashBalanceRepository.findTop1ByTypeAndReferenceId(journalInfo.getType(), 
				 String.valueOf(baseEntity.getId()) );
		
		log.info("existing balance:{}", balance);
		
		return balance;
	}
	
	/**
	 * get balance at the end of month
	 * @param month starts at 1
	 * @param year
	 * @param isDonationThrusday
	 * @return
	 */
	public CashBalance getBalanceBefore (int month, int year, boolean isDonationThrusday) {  
		
		String dateString = getDateString(month, year);
		
		Object resultObject;
		if(isDonationThrusday) {
			resultObject 	= cashBalanceRepository.getDonationThrusdayBalanceBefore(dateString);
		}else {
			resultObject	= cashBalanceRepository.getBalanceBefore(dateString );
		}
		
		Object[] result = (Object[]) resultObject;
		int RESULT_LEGTH = 3; 
		
		if(result!= null && result.length == RESULT_LEGTH && isNotNull(result)) {
			return populateCashBalanceObject(result);
		}
		return new CashBalance();
	}
	
	public boolean isNotNull(Object[] array) {
		int size = array.length;
		
		for(int i = 0; i < size - 1; i++) {
			try {
				if(array[i] == null) {
					return false;
				}
			}catch (Exception e) { 
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param resultObject, 3 sized array
	 * @return
	 */
	private static CashBalance populateCashBalanceObject(Object[] resultObject) {
		CashBalance cashBalance = new CashBalance();
		cashBalance.setCreditAmount(Long.valueOf(String.valueOf(resultObject[0] )));
		cashBalance.setDebitAmount(Long.valueOf(String.valueOf(resultObject[1] )));
		cashBalance.setActualBalance(Long.valueOf(String.valueOf(resultObject[2] )));
		return cashBalance;
	}
	
	/**
	 * get balance at the end of month
	 * @param month starts at 1
	 * @param year
	 * @param isDonationThrusday
	 * @return
	 */
	public CashBalance getOrphanFundBalanceBefore (int month, int year ) { 
		
		long debitValue = getFundFlowNominal(month, year, OrphanCashflowType.DONATION);
		long creditValue = getFundFlowNominal(month, year, OrphanCashflowType.DISTRIBUTION);
		
		CashBalance result = new CashBalance();
		result.setDebitAmount(debitValue);
		result.setCreditAmount(creditValue);
		return result ;
	}

	private long getFundFlowNominal(int month, int year, OrphanCashflowType cashflowType) {
 
		String dateString = getDateString(month, year);
		
		long  Nominal = 0L; 
		try{
			Object result = donationOrphanRepository.getCashflowBefore(dateString, cashflowType.toString());
			Nominal = Long.valueOf(result.toString());
		}catch (Exception e) {
			log.error("Error parsing orphan "+cashflowType);
			e.printStackTrace();
		}
		
		return Nominal;
	}
	
	private String getDateString(int month, int year) {
		Date date = DateUtil.getDate(year, month-1, 1);
		String dateString = DateUtil.formatDate(date, DATE_FORMAT);		
		return dateString;
	}

	/**
	 * set values for cash balance based on given entity
	 * @param financialEntity
	 * @return
	 */
	public static CashBalance mapCashBalance(FinancialEntity financialEntity) { 
		
		 BalanceJournalInfo journalInfo =  financialEntity.getBalanceJournalInfo(); 
		 return journalInfo.getBalanceObject();
	} 
	
	/**
	 * update balance
	 * @param baseEntity
	 */
	public synchronized void updateCashBalance(FinancialEntity baseEntity) {
		
		if(baseEntity == null || baseEntity.getId() == null) {
			return;
		} 
		
		CashBalance existingRecord = getBalanceByTransactionItem(baseEntity);
		
		CashBalance cashBalance = existingRecord == null ? new CashBalance() : existingRecord;
		
		final CashBalance mappedCashBalanceInfo = mapCashBalance(baseEntity);
		final long creditAmount = mappedCashBalanceInfo.getCreditAmount();
		final long debitAmount = mappedCashBalanceInfo.getDebitAmount();
		final String info = mappedCashBalanceInfo.getReferenceInfo(); 
		final Date date = mappedCashBalanceInfo.getDate();
		 
		cashBalance.setDebitAmount(debitAmount);
		cashBalance.setCreditAmount(creditAmount); 
		cashBalance.setReferenceInfo(info);
		cashBalance.setReferenceId(String.valueOf(baseEntity.getId()));
		cashBalance.setDate(date); 
		cashBalance.setModifiedDate(new Date());
		cashBalance.setType(mappedCashBalanceInfo.getType());
		
		entityRepository.save(cashBalance);
	}
	
	public int[] getTransactionYears() {
		int minYear = cashBalanceRepository.getMinYear();
		int maxYear = cashBalanceRepository.getMaxYear(); 
		
		int[] result = new int[maxYear-minYear+1];
		for(int i = minYear; i <= maxYear; i++) {
			result[i - minYear] = i;
		}
		return result;
	}
 

}
