package com.fajar.schoolmanagement.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.dto.RegisterStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto("Siswa")
@Entity
@Table(name = "student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3004105950283886322L;

	@JoinColumn(name = "parent_id")
	@ManyToOne
	@FormField(optionItemName = "fatherName", type = FieldType.FIELD_TYPE_DYNAMIC_LIST, lableName="Nama Orang Tua/Ayah") 
	private StudentParent studentParent;
	 
	@Column(name = "full_name")
	 
	@FormField(lableName = "Nama Lengkap")
	private String fullName;

	@Column(name = "name")
	@FormField( lableName = "Nama Panggilan") 
	private String name;

	@Column(name = "place_of_birth")
	@FormField(lableName="Tempat Lahir") 
	private String placeOfBirth;

	@Column(name = "date_of_birth")
	@FormField(lableName="Tanggal Lahir") 
	private Date dateOfBirth;

	@Column
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = { "Laki-laki", "Perempuan" }, lableName="Jenis Kelamin")
	 private String gender;

	@Column(name = "sibling_seq")
	@FormField(type = FieldType.FIELD_TYPE_NUMBER, lableName="Anak Ke")
	 private int siblingSequence;

	@Column(name = "siblings_count")
	@FormField(type = FieldType.FIELD_TYPE_NUMBER, lableName="Jumlah Saudara")
	 private int siblingsCount;
	
	@Column(name= "child_status")
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = { "Anak Kandung", "Anak Tiri", "Anak Adopsi" }, lableName="Status Ananda")
	 private String childStatus;

	@Column
	@FormField(lableName="Asal Sekolah")
	 private String school;

	@Column
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Alamat")
	 private String address;

	@Column(name = "image_url", unique = true)
	@FormField(type = FieldType.FIELD_TYPE_IMAGE, required = false, multiple = false, defaultValue = "Default.BMP", lableName="Foto")
	 private String imageUrl; 
	
	@Enumerated(EnumType.STRING)
	@FormField(type = FieldType.FIELD_TYPE_PLAIN_LIST, availableValues = {}, lableName = "Status Pendaftaran")
	@Column(name = "register_status", nullable = true)
	private RegisterStatus registerStatus;
	
	

}
