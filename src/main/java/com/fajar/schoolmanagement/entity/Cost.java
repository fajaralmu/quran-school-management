package com.fajar.schoolmanagement.entity;

import java.io.Serializable;

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
@Dto("Jenis Pengeluaran")
@Entity
@Table (name="school_cost_type")
@Data
@Builder	
@AllArgsConstructor
@NoArgsConstructor
public class Cost extends BaseEntity implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 4969863194918869183L;
	@FormField 
	@Column(unique = true)
	private String name;
	@FormField ( type= FieldType.FIELD_TYPE_TEXTAREA) 
	@Column
	private String description;
}
