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
import com.fajar.schoolmanagement.service.transaction.CashType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(editable = false, value="Jurnal Keuangan")
@Entity
@Table(name="fin_balance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashBalance extends BaseEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -1586384158220885834L;

	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	private long formerBalance;
	//add balance
	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	private long creditAmount;
	//reduce balance
	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	private long debitAmount;
	@FormField(type = FieldType.FIELD_TYPE_NUMBER)
	private long actualBalance;
	@FormField
	@Enumerated(EnumType.STRING)
	@Column
	private CashType type;
	@FormField
	private Date date;
	@FormField
	private String referenceId;
	@FormField
	private String referenceInfo; 
	
	
}
