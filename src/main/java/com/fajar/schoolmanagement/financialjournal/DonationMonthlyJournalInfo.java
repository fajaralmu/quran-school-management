package com.fajar.schoolmanagement.financialjournal;

import com.fajar.schoolmanagement.entity.DonationMonthly;

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

		referenceInfo = SourceOfFund.DONATION_MONTHLY + "_" + donationMonthly.getStudent().getName();
		date = donationMonthly.getDate();
	}

}
