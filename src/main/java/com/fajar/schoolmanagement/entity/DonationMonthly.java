package com.fajar.schoolmanagement.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto ("Infaq Bulanan Siswa")
@Entity
@Table(name = "donation_montly")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationMonthly extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6473541136274411199L;

	 
	@FormField
	@Column
	private Date date;
	
	@JoinColumn(name = "student_id")
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_DYNAMIC_LIST, optionItemName = "fullName")
	private Student student;
	
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"})
	@Column
	private int month;
	
	@FormField
	@Column
	private int year;
	
	@FormField(type = FieldType.FIELD_TYPE_CURRENCY)
	@Column
	private long nominal;
	
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	
	/** $$$ **/
	@Override
	public Date getTransactionDate() { 
		return date;
	}
	@Override
	public String getTransactionName() {
		return "Infaq Bulanan";
	}
	@Override
	public long getTransactionNominal() { 
		return nominal;
	}
}
