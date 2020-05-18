package com.fajar.schoolmanagement.service.transaction;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.DonationThursday;
import com.fajar.schoolmanagement.repository.CashBalanceRepository;
import com.fajar.schoolmanagement.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class CashBalanceService {
	@Autowired
	private CashBalanceRepository cashBalanceRepository; 
	
	public CashBalance getBalanceByTransactionItem(BaseEntity baseEntity) {
		
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
		
		Date date = DateUtil.getDate(year, month-1, 1);
		String dateString = DateUtil.formatDate(date, "yyyy-MM-dd");
		
		Object object;
		if(isDonationThrusday) {
			object = cashBalanceRepository.getDonationThrusdayBalanceBefore(dateString);
		}else {
			object	= cashBalanceRepository.getBalanceBefore(dateString );
		}
		
		Object[] result = (Object[]) object;
		int RESULT_LEGTH = 3; 
		
		CashBalance cashBalance = new CashBalance();
		if(result!= null && result.length == RESULT_LEGTH && result[0] != null && result[1] != null && result[2] != null) {
			cashBalance.setCreditAmount(Long.valueOf(String.valueOf(result[0] )));
			cashBalance.setDebitAmount(Long.valueOf(String.valueOf(result[1] )));
			cashBalance.setActualBalance(Long.valueOf(String.valueOf(result[2] )));
		}
		return cashBalance;
	}
	
	/**
	 * set values for cash balance based on given entity
	 * @param baseEntity
	 * @return
	 */
	public static CashBalance mapCashBalance(BaseEntity baseEntity) { 
		
		 BalanceJournalInfo journalInfo = getJournalInfo(baseEntity);
		 
		 return journalInfo.getBalanceObject();
	}
	
	private static BalanceJournalInfo getJournalInfo(BaseEntity baseEntity) {
		if(baseEntity instanceof CostFlow) {
			return new CostJournalInfo((CostFlow) baseEntity); 
		}else if(baseEntity instanceof CapitalFlow) {
			return new FundJournalInfo((CapitalFlow) baseEntity);
		}else if(baseEntity instanceof DonationMonthly) {
			return new DonationMonthlyJournalInfo((DonationMonthly) baseEntity);
		}else if(baseEntity instanceof DonationThursday){
			return new DonationTrhusdayJournalInfo((DonationThursday) baseEntity);
		}else {
			return null;
		}
	}
	
	/**
	 * update balance
	 * @param baseEntity
	 */
	public synchronized void updateCashBalance(BaseEntity baseEntity) {
		
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
		
		cashBalanceRepository.save(cashBalance);
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
