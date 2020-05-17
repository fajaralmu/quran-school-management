package com.fajar.schoolmanagement.financialjournal;

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
		referenceInfo = "COST_"+costFlow.getCostType().getName();
		date = costFlow.getDate();
	 
	}

	 

}
