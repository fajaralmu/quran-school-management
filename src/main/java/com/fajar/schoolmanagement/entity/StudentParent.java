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

@Dto("Wali Siswa")
@Entity
@Table (name="student_parent")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentParent extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5455451366947342120L;
	@FormField
	@Column(name="father_name")
	private String fatherName;
	
	@FormField
	@Column(name="mother_name")
	private String motherName;
	
	@FormField
	@Column(name="father_birth_place")
	private String fatherPlaceOfBirth;
	
	@FormField
	@Column(name="mother_birth_place")
	private String motherPlaceOfBirth;
	
	@FormField
	@Column(name="father_date_of_birth")
	private Date fatherDateOfBirth;
	
	@FormField
	@Column(name="mother_date_of_birth")
	private Date motherDateOfBirth;
	
	@FormField
	@Column(name="father_education")
	private String fatherEducation;
	
	@FormField
	@Column(name="mother_education")
	private String motherEducation;
	
	@FormField
	@Column(name="father_occupation")
	private String fatherOccupation;
	
	@FormField
	@Column(name="mother_occupation")
	private String motherOccupation;
	
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column(name="address")
	private String address;
	
	@FormField
	@Column(name="phone_number")
	private String phoneNumber;

}
