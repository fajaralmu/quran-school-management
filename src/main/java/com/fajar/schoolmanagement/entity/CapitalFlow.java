package com.fajar.schoolmanagement.entity;

import java.io.Serializable;
import java.rmi.Remote;
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

@Dto
@Entity
@Table(name = "fund_flow")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapitalFlow extends BaseEntity implements Remote, Serializable {

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

	@JoinColumn(name = "fund_id")
	@ManyToOne
	@FormField(type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private Capital fundType;

}
