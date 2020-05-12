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

@Dto(ignoreBaseField = false)
@Entity
@Table(name = "school_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile extends BaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 4095664637854922384L;
	@Column(unique = true)
	@FormField
	private String name;
	@Column(name = "mart_code", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_HIDDEN)
	private String martCode;
	@Column(name = "short_description")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String shortDescription;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String about;
	@Column(name = "welcoming_message")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String welcomingMessage;
	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String address;

	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	private String contact;
	@Column
	@FormField
	private String website;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultBackground.BMP")
	@Column(name = "background_url")
	private String backgroundUrl;

}
