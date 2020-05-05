package com.fajar.schoolmanagement.entity;

import java.io.Serializable;

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
@Table(name="menu")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu extends BaseEntity implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -6895624969478733293L;

	@FormField
	@Column
	private String code;
	@FormField
	@Column
	private String name;
	@FormField(type = FormField.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	@FormField
	@Column
	private String url; 
	@JoinColumn(name="page_id", nullable = false)
	@ManyToOne
	@FormField(lableName="Page",   type=FormField.FIELD_TYPE_FIXED_LIST, optionItemName="name")
	private Page menuPage;
	@FormField(type = FormField.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;
	 

}
