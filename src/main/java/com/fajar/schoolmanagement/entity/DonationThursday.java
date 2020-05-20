package com.fajar.schoolmanagement.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto ("Infaq Kamis")
@Entity
@Table(name = "donation_thrusday")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationThursday extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8907113168071161539L;
	
	@Column
	@FormField
	private Date date;
	
	@Column
	@FormField(type = FieldType.FIELD_TYPE_CURRENCY)
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
		return "Infaq Kamis";
	}
	@Override
	public long getTransactionNominal() { 
		return nominal;
	}
}
