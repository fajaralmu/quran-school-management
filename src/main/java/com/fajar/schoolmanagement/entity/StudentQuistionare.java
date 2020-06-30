package com.fajar.schoolmanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.AdditionalQuestionField;
import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.dto.FormInputColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto(value = "Kuisioner Siswa",formInputColumn = FormInputColumn.TWO_COLUMN, quistionare = true)
@Entity
@Table (name="student_questionare")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuistionare extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2421546330168748734L;
	static final String STUDENT_IDENTITY = "Identitas Anak";
	static final String PARENT_IDENTITY = "Identitas Orang Tua";
	static final String CHALLENGE_AND_OBSTACLE = "Tantangan Dan Hambatan";
	static final String MOTIVATION_AND_HOPE = "Motivasi Dan Harapan";
	static final String PARENT_PARTICIPATION = "Partisipasi Orang Tua";
	
	@JoinColumn(name = "student_id")
	@ManyToOne
	@FormField(optionItemName = "name", type = FieldType.FIELD_TYPE_DYNAMIC_LIST, lableName="Nama Siswa")
	@AdditionalQuestionField(STUDENT_IDENTITY)
	private Student student;
	
	@Column(name = "illness_history")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Riwayat Penyakit")
	@AdditionalQuestionField(STUDENT_IDENTITY)
	private String illnesHistory;
	
	@Column(name = "quest_1")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Apakah Ananda Melalui Fase Merangkak")
	@AdditionalQuestionField(STUDENT_IDENTITY)
	private String quest1;
	
	@Column(name = "quest_2")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Sakit Yang Sedang Di Alami")
	@AdditionalQuestionField(STUDENT_IDENTITY)
	private String quest2;
	
	@Column(name = "quest_3")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Kebutuhan Khusus yang Perlu Diperhatikan Dari Ananda")
	@AdditionalQuestionField(STUDENT_IDENTITY)
	private String quest3;
	
	@Column(name = "quest_4")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Catatan Orang Tua Tentang Ananda Secara Umum")
	@AdditionalQuestionField(STUDENT_IDENTITY)
	private String quest4;
	
	/////////MOTIVATION_AND_HOPE////////////
	
	@Column(name = "quest_5")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Apakah Bapak dan Ibu setuju dan sudah saling menyepakati untuk ananda belajar di KB Zainun Nafi")
	@AdditionalQuestionField(MOTIVATION_AND_HOPE)
	private String quest5;
	
	@Column(name = "quest_6")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Apa motivasi dan keinginan orang tua sehingga ananda dimasukkan ke KB Zainun Nafi")
	@AdditionalQuestionField(MOTIVATION_AND_HOPE)
	private String quest6;
	
	@Column(name = "quest_7")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Harapan apa yang dimiliki oleh orang tua terhadap ananda dalam belajar di KB Zainun Nafi ini")
	@AdditionalQuestionField(MOTIVATION_AND_HOPE)
	private String quest7;
	
	@Column(name = "quest_8")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Apa yang orang tua lakukan untuk memenuhi harapan tersebut")
	@AdditionalQuestionField(MOTIVATION_AND_HOPE)
	private String quest8;
	
	@Column(name = "quest_9")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Apa yang orang tua akan lakukan apabila harapan tersebut tidak terpenuhi")
	@AdditionalQuestionField(MOTIVATION_AND_HOPE)
	private String quest9;
	
	//////////////////////PARENT_PARTICIPATION/////////////////////
	
	@Column(name = "quest_10")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Bersama siapa saja ananda tinggal dirumah")
	@AdditionalQuestionField(PARENT_PARTICIPATION)
	private String quest10;
	
	@Column(name = "quest_11")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Bagaimana Pola Asuh Ananda Dirumah secara umum")
	@AdditionalQuestionField(PARENT_PARTICIPATION)
	private String quest11;
	
	@Column(name = "quest_12")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Siapa yang memiliki kedekatan dengan ananda")
	@AdditionalQuestionField(PARENT_PARTICIPATION)
	private String quest12;
	
	@Column(name = "quest_13")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Sejauh mana keterlibatan orang tua dalam kegiatan ananda")
	@AdditionalQuestionField(PARENT_PARTICIPATION)
	private String quest13;
	
	@Column(name = "quest_14")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Bagaimana kesiapan orang tua dalam hal pembayaran kegiatan ini dengan peraturan sbb? (masuk atau tidak biaya tetap setap bulanan dan dibayarkan paling lambat tanggal 10")
	@AdditionalQuestionField(PARENT_PARTICIPATION)
	private String quest14;
	
	//////////////////////////CHALLENGE_AND_OBSTACLE//////////////////
	
	@Column(name = "quest_15")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Jika ananda 'macet' untuk berangkat sekolah apa yang akan Bapak/Ibu lakukan?")
	@AdditionalQuestionField(CHALLENGE_AND_OBSTACLE)
	private String quest15;
	
	@Column(name = "quest_16")
	@FormField(type = FieldType.FIELD_TYPE_TEXTAREA, lableName="Apa cara khusus yang Bapak/Ibu lakukan untuk memotivasi ananda")
	@AdditionalQuestionField(CHALLENGE_AND_OBSTACLE)
	private String quest16;
}
