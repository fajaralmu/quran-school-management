package com.fajar.schoolmanagement.financialjournal;

import com.fajar.schoolmanagement.entity.CapitalFlow;

public class FundJournalInfo extends BalanceJournalInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -293357633959471496L;
	final CapitalFlow capitalFlow;
	
	public FundJournalInfo(CapitalFlow capitalFlow) {
		this.capitalFlow = capitalFlow; 
		
		buildBalanceObject();
	}

	@Override
	public void buildBalanceObject() {  
		debitAmount = capitalFlow.getNominal();
		cashType = CashType.GENERAL_COST;
		referenceInfo = "FUND_"+capitalFlow.getFundType().getName();
		date = capitalFlow.getDate();
	}

}
