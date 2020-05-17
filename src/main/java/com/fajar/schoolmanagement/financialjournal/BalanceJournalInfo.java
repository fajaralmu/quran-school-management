package com.fajar.schoolmanagement.financialjournal;

import java.io.Serializable;
import java.util.Date;

import com.fajar.schoolmanagement.entity.CashBalance;

import lombok.Data;

@Data
public abstract class BalanceJournalInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8648679391834607263L;
	protected Date date;
	protected long debitAmount;
	protected long creditAmount;
	protected String referenceInfo;
	protected CashType cashType;
	protected CashBalance balanceObject;

	public CashBalance getBalanceObject() {
		return CashBalance.builder().date(date).creditAmount(creditAmount).debitAmount(debitAmount)
				.type(cashType)
				.referenceInfo(referenceInfo).build();
	}

	public abstract void buildBalanceObject();

}
