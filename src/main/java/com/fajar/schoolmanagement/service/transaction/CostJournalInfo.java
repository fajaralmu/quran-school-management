package com.fajar.schoolmanagement.service.transaction;

import com.fajar.schoolmanagement.entity.CostFlow;

public class CostJournalInfo extends BalanceJournalInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6427982548966366819L;
	final CostFlow costFlow;

	public CostJournalInfo(CostFlow costFlow) {
		this.costFlow = costFlow;
		
		buildBalanceObject();
	}
 
	@Override
	public void buildBalanceObject() {  
		creditAmount = costFlow.getNominal(); 
		cashType = determineCashType();
		referenceInfo = cashType+"_"+costFlow.getCostType().getName();
		date = costFlow.getDate();
	 
	}
	
	private CashType determineCashType() {
		if(costFlow.getFundSource().equals(SourceOfFund.DONATION_THRUSDAY)) {
			return CashType.DONATION_THURSDAY;
		}
		
		return CashType.GENERAL_COST;
	}

	 

}
