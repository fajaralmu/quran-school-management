package com.fajar.schoolmanagement.entity;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.service.transaction.SourceOfFund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto("Aliran Pengeluaran")
@Entity
@Table(name = "school_cost_flow")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostFlow extends BaseEntity implements Remote, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6143899665323318955L;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_DATE)
	private Date date;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String description;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_CURRENCY)
	private long nominal;

	@JoinColumn(name = "cost_id")
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name", lableName ="Jenis Pengeluaran")
	private Cost costType; 
	
	@Column(name="source_of_fund")
	@Enumerated(EnumType.STRING)
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = {})
	private SourceOfFund fundSource;
	
	/** $$$ **/
	@Override
	public Date getTransactionDate() { 
		return date;
	}
	@Override
	public String getTransactionName() {
		return description;
	}
	@Override
	public long getTransactionNominal() { 
		return nominal;
	}

}
