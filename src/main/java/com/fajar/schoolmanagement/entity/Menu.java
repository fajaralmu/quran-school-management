package com.fajar.schoolmanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.service.entity.EntityUpdateInterceptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(ignoreBaseField = false)
@Entity
@Table(name = "menu")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu extends BaseEntity {
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
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	@FormField
	@Column
	private String url;
	@JoinColumn(name = "page_id", nullable = false)
	@ManyToOne
	@FormField(lableName = "Page", type = FieldType.FIELD_TYPE_FIXED_LIST, optionItemName = "name")
	private Page menuPage;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "icon_url")
	private String iconUrl;

	@Override
	public EntityUpdateInterceptor getUpdateInterceptor() {

		return new EntityUpdateInterceptor<Menu>() {

			@Override
			public Menu preUpdate(Menu menu) {
				if (menu.getUrl().startsWith("/") == false) {
					menu.setUrl("/" + menu.getUrl());
				}
				return menu;

			}
		};
	}

}
