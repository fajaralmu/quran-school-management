package com.fajar.schoolmanagement.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.dto.FormInputColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(formInputColumn = FormInputColumn.ONE_COLUMN, ignoreBaseField = false, value ="Halaman Aplikasi")
@Entity
@Table(name = "page")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Page extends BaseEntity implements Sequenced {
	/**
	* 
	*/
	private static final long serialVersionUID = -4180675906997901285L;

	@FormField
	@Column(unique = true)
	private String code;
	@FormField
	@Column(unique = true)
	private String name;
	@FormField(lableName = "Authorized (true:1 or false:0)", type = FieldType.FIELD_TYPE_NUMBER)
	@Column(nullable = false)
	private int authorized;
	@FormField(lableName = "Is non-menu Page(true:1 or false:0)", type = FieldType.FIELD_TYPE_NUMBER)
	@Column(name = "is_non_menu_page")
	private int nonMenuPage;
	@FormField(lableName = "Link for non menu page")
	@Column(unique = true)
	private String link;
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA)
	@Column
	private String description;
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, defaultValue = "DefaultIcon.BMP")
	@Column(name = "image_url")
	private String imageUrl;
	@Column()
	@FormField(type=FieldType.FIELD_TYPE_NUMBER,lableName = "Urutan Ke")
	private int sequence;

	@Transient
	private List<Menu> menus;
	 
	public String isMenuPage() {
		return nonMenuPage == 0 ? "true":"false"; 
	}
}
