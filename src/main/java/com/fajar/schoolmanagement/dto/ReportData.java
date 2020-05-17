package com.fajar.schoolmanagement.dto;

import java.io.Serializable;
import java.util.List;

import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CashBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723760671439954923L;
	
	private List<BaseEntity> funds;
	private List<BaseEntity> spendings;
	private CashBalance initialBalance;
	private Filter filter;

}
