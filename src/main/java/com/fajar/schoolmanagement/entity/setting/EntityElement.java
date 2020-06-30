package com.fajar.schoolmanagement.entity.setting;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.fajar.schoolmanagement.annotation.AdditionalQuestionField;
import com.fajar.schoolmanagement.annotation.BaseField;
import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.exception.InvalidValueException;
import com.fajar.schoolmanagement.util.EntityUtil;
import com.fajar.schoolmanagement.util.MyJsonUtil;
import com.fajar.schoolmanagement.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Dto
@Slf4j
public class EntityElement implements Serializable {

	static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	/**
	 * 
	 */
	private static final long serialVersionUID = -6768302238247458766L;
	private String id;
	private String type;
	private String className;
	private boolean identity;
	private boolean required;
	private boolean idField;
	private String lableName;
	private List<BaseEntity> options;
	private String jsonList;
	private String optionItemName;
	private String optionValueName;
	private String entityReferenceName;
	private String entityReferenceClass;
	private boolean multiple;
	private boolean showDetail;
	private String detailFields;
	private String[] defaultValues;
	private List<Object> plainListValues;
	
	private boolean isGrouped;
	private String inputGroupname;

	private boolean detailField;

	// not shown in view

	public final Field field;
	public final boolean ignoreBaseField;
	public EntityProperty entityProperty;
	public Map<String, List<?>> additionalMap;

	private FormField formField;
	private BaseField baseField;
	private boolean skipBaseField;
	private boolean hasJoinColumn;

	public EntityElement(Field field, EntityProperty entityProperty) {
		this.field = field;
		this.ignoreBaseField = entityProperty.isIgnoreBaseField();
		this.entityProperty = entityProperty;
		init();
	}

	public EntityElement(Field field, EntityProperty entityProperty, Map<String, List<?>> additionalMap) {
		this.field = field;
		this.ignoreBaseField = entityProperty.isIgnoreBaseField();
		this.entityProperty = entityProperty;
		this.additionalMap = additionalMap;
		init();
	}

	private void init() {
		formField = field.getAnnotation(FormField.class);
		baseField = field.getAnnotation(BaseField.class);

		idField = field.getAnnotation(Id.class) != null;
		skipBaseField = !idField && (baseField != null && ignoreBaseField);

		identity = idField;
		hasJoinColumn = field.getAnnotation(JoinColumn.class) != null;

		checkIfGroupedInput();
	}
	
	public String getJsonListString(boolean removeBeginningAndEndIndex) {
		try {
			String jsonStringified = OBJECT_MAPPER.writeValueAsString(jsonList).trim();
			if(removeBeginningAndEndIndex) {
				StringBuilder stringBuilder = new StringBuilder(jsonStringified);
				stringBuilder.setCharAt(0, ' ');
				stringBuilder.setCharAt(jsonStringified.length() - 1, ' ');
				return stringBuilder.toString().trim();
			}
			return jsonStringified;
		}catch (Exception e) {
			return "{}";
		}
	}

	private void checkIfGroupedInput() {
		AdditionalQuestionField annotation = field.getAnnotation(AdditionalQuestionField.class);
		isGrouped = annotation != null;
		inputGroupname = isGrouped ? annotation.value() : "0";
	}

	public boolean build() throws Exception {
		boolean result = doBuild();
		setEntityProperty(null);
		return result;
	}

	private boolean doBuild() throws Exception {

		boolean formFieldIsNullOrSkip = (formField == null || skipBaseField);
		if (formFieldIsNullOrSkip) {
			return false;
		}

		String lableName = formField.lableName().equals("") ? field.getName() : formField.lableName();
		FieldType determinedFieldType = determineFieldType();

		try {

			checkFieldType(determinedFieldType);
			boolean hasJoinColumn = field.getAnnotation(JoinColumn.class) != null;

			if (hasJoinColumn) {
				processJoinColumn(determinedFieldType);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}

		checkDetailField();
		setId(field.getName());
		setIdentity(idField);
		setLableName(StringUtil.extractCamelCase(lableName));
		setRequired(formField.required());
		setType(determinedFieldType.value);
		setMultiple(formField.multiple());
		setClassName(field.getType().getCanonicalName());
		setShowDetail(formField.showDetail());
		return true;
	}

	private void checkDetailField() {

		if (formField.detailFields().length > 0) {
			setDetailFields(String.join("~", formField.detailFields()));
		}
		if (formField.showDetail()) {
			setOptionItemName(formField.optionItemName());
			setDetailField(true);
		}
	}

	private void checkFieldType(FieldType fieldType) throws Exception {

		if (fieldType.equals(FieldType.FIELD_TYPE_IMAGE)) {
			processImageType();

		} else if (fieldType.equals(FieldType.FIELD_TYPE_CURRENCY)) {
			processCurrencyType();

		} else if (fieldType.equals(FieldType.FIELD_TYPE_DATE)) {
			processDateType();

		} else if (fieldType.equals(FieldType.FIELD_TYPE_PLAIN_LIST)) {
			processPlainListType();

		}

	}

	private void processCurrencyType() {
		entityProperty.getCurrencyElements().add(field.getName());
	}

	private void processImageType() {
		entityProperty.getImageElements().add(field.getName());
	}

	private void processDateType() {
		entityProperty.getDateElements().add(field.getName());
	}

	private void processPlainListType() throws Exception {

		String[] availableValues = formField.availableValues();

		if (availableValues.length > 0) {
			setPlainListValues(Arrays.asList(availableValues));

		} else if (field.getType().isEnum()) {
			Object[] enumConstants = field.getType().getEnumConstants();
			setPlainListValues(Arrays.asList(enumConstants));

		} else {
			log.error("Ivalid element: {}", field.getName());
			throw new Exception("Invalid Element");
		}
	}

	private FieldType determineFieldType() {

		FieldType fieldType;

		if (EntityUtil.isNumericField(field)) {
			fieldType = FieldType.FIELD_TYPE_NUMBER;

		} else if (field.getType().equals(Date.class) && field.getAnnotation(JsonFormat.class) == null) {
			fieldType = FieldType.FIELD_TYPE_DATE;

		} else if (idField) {
			fieldType = FieldType.FIELD_TYPE_HIDDEN;
		} else {
			fieldType = formField.type();
		}
		return fieldType;
	}

	private void processJoinColumn(FieldType fieldType) throws Exception {
		log.info("field {} of {} is join column, type: {}", field.getName(), field.getDeclaringClass(), fieldType);

		Class<?> referenceEntityClass = field.getType();
		Field referenceEntityIdField = EntityUtil.getIdFieldOfAnObject(referenceEntityClass);

		if (referenceEntityIdField == null) {
			throw new Exception("ID Field Not Found");
		}

		if (fieldType.equals(FieldType.FIELD_TYPE_FIXED_LIST) && additionalMap != null) {

			List<BaseEntity> referenceEntityList = (List<BaseEntity>) additionalMap.get(field.getName());
			if (null == referenceEntityList || referenceEntityList.size() == 0) {
				throw new InvalidValueException(
						"Invalid object list provided for key: " + field.getName() + " in EntityElement.AdditionalMap");
			}
			log.info("Additional map with key: {} . Length: {}", field.getName(), referenceEntityList.size());
			if (referenceEntityList != null) {
				setOptions(referenceEntityList);
				setJsonList(MyJsonUtil.listToJson(referenceEntityList));
			}

		} else if (fieldType.equals(FieldType.FIELD_TYPE_DYNAMIC_LIST)) {

			setEntityReferenceClass(referenceEntityClass.getSimpleName());
		}

		setOptionValueName(referenceEntityIdField.getName());
		setOptionItemName(formField.optionItemName());
	}

}
