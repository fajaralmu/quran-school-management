package com.fajar.schoolmanagement.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.FinancialEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723760671439954923L;
	
	private List<FinancialEntity> funds;
	private List<FinancialEntity> spendings;
	private CashBalance initialBalance;
	private Filter filter;

}
