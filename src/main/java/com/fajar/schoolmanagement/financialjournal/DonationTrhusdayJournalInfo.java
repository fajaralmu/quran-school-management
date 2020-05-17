package com.fajar.schoolmanagement.financialjournal;

import com.fajar.schoolmanagement.entity.DonationThursday;

public class DonationTrhusdayJournalInfo extends BalanceJournalInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -293357633959471496L;
	final DonationThursday donationThrusday;
	
	public DonationTrhusdayJournalInfo(DonationThursday capitalFlow) {
		this.donationThrusday = capitalFlow; 
		
		buildBalanceObject();
	}

	@Override
	public void buildBalanceObject() {  
		debitAmount = donationThrusday.getNominal();
		
		referenceInfo = SourceOfFund.DONATION_THRUSDAY.toString();
		date = donationThrusday.getDate();
	}

}
