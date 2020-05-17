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

@Dto
@Entity
@Table(name = "teacher")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends BaseEntity {
	/**
	* 
	*/
	private static final long serialVersionUID = 417517979177930249L;

	@Column
	@FormField
	private String name;

	@Column(name = "place_of_birth")
	@FormField
	private String placeOfBirth;

	@Column(name = "date_of_birth")
	@FormField(type = FieldType.FIELD_TYPE_DATE)
	private Date dateOfBirth;

	@Column
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = { "Laki-laki", "Perempuan" })
	private String gender;

	@Column(name = "phone_number")
	@FormField
	private String phoneNumber;

	@Column(name = "formal_education")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String formalEducation;

	@Column(name = "nonformal_education")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String nonFormalEducation;

	@Column(name = "organizational_experience")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String organizationalExperience;

	@Column(name = "training_experience")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String trainingExperience;

	@Column(name = "work_experience")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String workExperience;

	@Column(name = "current_activity")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String currentActivity;

	@Column(name = "image_url", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, multiple = false, defaultValue = "Default.BMP")
	private String imageUrl;
}
