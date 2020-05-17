package com.fajar.schoolmanagement.financialjournal;

import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.DonationThursday;

public class DonationMonthlyJournalInfo extends BalanceJournalInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -293357633959471496L;
	final DonationMonthly donationMonthly;
	
	public DonationMonthlyJournalInfo(DonationMonthly capitalFlow) {
		this.donationMonthly = capitalFlow; 
		
		buildBalanceObject();
	}

	@Override
	public void buildBalanceObject() {  
		debitAmount = donationMonthly.getNominal();
		
		referenceInfo = "Donation_Monthly_"+donationMonthly.getStudent().getName();
		date = donationMonthly.getDate();
	}

}
