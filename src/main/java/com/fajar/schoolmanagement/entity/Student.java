package com.fajar.schoolmanagement.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Entity
@Table (name="student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3004105950283886322L;
	
	@Column(name="full_name")
	@FormField
	private String fullName;
	
	@Column(name="name")
	@FormField
	private String name;
	
	@Column(name="place_of_birth")
	@FormField
	private String placeOfBirth;
	
	@Column(name="date_of_birth")
	@FormField
	private Date dateOfBirth;
	
	@Column
	@FormField
	private String gender;
	
	@Column(name="sibling_seq")
	@FormField(type = FormField.FIELD_TYPE_NUMBER)
	private int siblingSequence;
	
	@Column(name="siblings_count")
	@FormField(type = FormField.FIELD_TYPE_NUMBER)
	private int siblingsCount;
	
	@Column
	@FormField
	private String school;
	
	@Column
	@FormField(type = FormField.FIELD_TYPE_TEXTAREA)
	private String address;
	
	@Column(name = "image_url", unique = true)
	@FormField(type = FormField.FIELD_TYPE_IMAGE, required = false, multiple = false, defaultValue = "Default.BMP")
	private String imageUrl;
	
	@JoinColumn(name = "parent_id")
	@ManyToOne
	@FormField(optionItemName = "fatherName", type = FormField.FIELD_TYPE_DYNAMIC_LIST)
	private StudentParent studentParent;

}
