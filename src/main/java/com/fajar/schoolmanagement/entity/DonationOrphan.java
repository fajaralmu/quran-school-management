package com.fajar.schoolmanagement.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.service.transaction.OrphanCashflowType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto("Dana Yatim")
@Entity
@Table(name = "school_orphan_donation")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationOrphan extends BaseEntity implements  Serializable {

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
	
	 
	@Column(name="cashflow_type")
	@Enumerated(EnumType.STRING)
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = {}, lableName = "Tipe Aliran Dana")
	private OrphanCashflowType cashflowType;
	
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
