package com.fajar.schoolmanagement.entity;

import java.util.Date;

public interface FinancialEntity {

	public Date getTransactionDate();
	public String getTransactionName();
	public long getTransactionNominal();
}
