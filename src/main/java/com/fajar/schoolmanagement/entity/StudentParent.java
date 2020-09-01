package com.fajar.schoolmanagement.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.AdditionalQuestionField;
import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(value = "Wali Siswa", quistionare = true)
@Entity
@Table(name = "student_parent")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentParent extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5455451366947342120L;
	static final String DATA_AYAH = "Identitas Ayah";
	static final String DATA_IBU = "Identitas Ibu";
	static final String DATA_ALL = "Lain-lain"; 

	@FormField
	@Column(name = "father_name")
	@AdditionalQuestionField(DATA_AYAH)
	private String fatherName;

	@FormField
	@Column(name = "father_birth_place")
	@AdditionalQuestionField(DATA_AYAH)
	private String fatherPlaceOfBirth;

	@FormField
	@Column(name = "father_date_of_birth")
	@AdditionalQuestionField(DATA_AYAH)
	private Date fatherDateOfBirth;

	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = {"-", "SD", "SMP", "SMA", "D1", "D2", "D3", "D4", "S1", "S2", "S3"	})
	@Column(name = "father_education")
	@AdditionalQuestionField(DATA_AYAH)
	private String fatherEducation;

	@FormField
	@Column(name = "father_occupation")
	@AdditionalQuestionField(DATA_AYAH)
	private String fatherOccupation;

	@FormField
	@Column(name = "mother_name")
	@AdditionalQuestionField(DATA_IBU)
	private String motherName;

	@FormField
	@Column(name = "mother_birth_place")
	@AdditionalQuestionField(DATA_IBU)
	private String motherPlaceOfBirth;

	@FormField
	@Column(name = "mother_date_of_birth")
	@AdditionalQuestionField(DATA_IBU)
	private Date motherDateOfBirth;

	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = {"-", "SD", "SMP", "SMA", "D1", "D2", "D3", "D4", "S1", "S2", "S3"	})
	@Column(name = "mother_education")
	@AdditionalQuestionField(DATA_IBU)
	private String motherEducation;

	@FormField
	@Column(name = "mother_occupation")
	@AdditionalQuestionField(DATA_IBU)
	private String motherOccupation;

	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column(name = "address")
	@AdditionalQuestionField(DATA_ALL)
	private String address;

	@FormField
	@Column(name = "phone_number")
	@AdditionalQuestionField(DATA_ALL)
	private String phoneNumber;

}
