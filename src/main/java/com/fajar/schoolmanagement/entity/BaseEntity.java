package com.fajar.schoolmanagement.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import org.apache.commons.lang3.NotImplementedException;

import com.fajar.schoolmanagement.annotation.BaseField;
import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.service.transaction.BalanceJournalInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Dto
@MappedSuperclass 
public class BaseEntity implements Serializable{
	 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8161890497812023383L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@FormField
	@Column
	private Long id;
	@BaseField
	@Column(name="general_color")
	@FormField(type = FieldType.FIELD_TYPE_COLOR, defaultValue = "green")
	private String color;
	@BaseField
	@Column(name="font_color")
	@FormField(type = FieldType.FIELD_TYPE_COLOR, defaultValue = "yellow")
	private String fontColor;
	 
	@Column(name="created_date")
	@JsonIgnore
//	@FormField
	private Date createdDate;
	@Column(name="modified_date")
	@JsonIgnore
	private Date modifiedDate;
	@Column(name="deleted")
	@JsonIgnore
	private boolean deleted;
	
	
	
	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@PrePersist
	private void prePersist() {
		if(this.createdDate == null) {
			this.createdDate = new Date();
		}
		this.modifiedDate = new Date();
	} 
	
	
}
