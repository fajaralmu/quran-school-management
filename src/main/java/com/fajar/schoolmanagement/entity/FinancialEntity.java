package com.fajar.schoolmanagement.entity;

import java.util.Date;

import com.fajar.schoolmanagement.service.transaction.BalanceJournalInfo;

public interface FinancialEntity {

	public Date getTransactionDate();
	public String getTransactionName();
	public long getTransactionNominal();
	public BalanceJournalInfo getBalanceJournalInfo();
	public Long getId();
}
