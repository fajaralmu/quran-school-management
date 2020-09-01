package com.fajar.schoolmanagement.entity.setting;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Id;

import org.slf4j.LoggerFactory;

import com.fajar.schoolmanagement.annotation.AdditionalQuestionField;
import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.util.CollectionUtil;
import com.fajar.schoolmanagement.util.MapUtil;
import com.fajar.schoolmanagement.util.MyJsonUtil;

public class EntityPropertyBuilder {
	org.slf4j.Logger log = LoggerFactory.getLogger(EntityPropertyBuilder.class);
	private final Class<?> clazz;
	private final Map<String, List<?>> additionalObjectList;

	public EntityPropertyBuilder(Class<?> clazz, Map<String, List<?>> additionalObjectList) {
		this.clazz = clazz;
		this.additionalObjectList = additionalObjectList;
	}

	public EntityPropertyBuilder(Class<?> clazz) {
		this.clazz = clazz;
		this.additionalObjectList = null;
	}

	public EntityProperty createEntityProperty() throws Exception {
		if (clazz == null || getClassAnnotation(clazz, Dto.class) == null) {
			return null;
		}

		Dto dto = (Dto) getClassAnnotation(clazz, Dto.class);
		final boolean ignoreBaseField = dto.ignoreBaseField();
		final boolean isQuestionare = dto.quistionare();

		EntityProperty entityProperty = EntityProperty.builder().ignoreBaseField(ignoreBaseField)
				.entityName(clazz.getSimpleName().toLowerCase()).isQuestionare(isQuestionare).build();
		try {

			List<Field> fieldList = getDeclaredFields();

			if (isQuestionare) {
				Map<String, List<Field>> groupedFields = sortListByQuestionareSection(fieldList);
				fieldList = CollectionUtil.mapOfListToList(groupedFields);
				Set<String> groupKeys = groupedFields.keySet();
				String[] keyNames = CollectionUtil.toArrayOfString(groupKeys.toArray());

				entityProperty.setGroupNames(keyNames);
			}
			List<EntityElement> entityElements = new ArrayList<>();
			List<String> fieldNames = new ArrayList<>();
			String fieldToShowDetail = "";

			for (Field field : fieldList) {

				final EntityElement entityElement = new EntityElement(field, entityProperty, additionalObjectList);

				if (false == entityElement.build()) {
					continue;
				}
				if (entityElement.isDetailField()) {
					fieldToShowDetail = entityElement.getId();
				}

				fieldNames.add(entityElement.getId());
				entityElements.add(entityElement);
			}

			entityProperty.setAlias(dto.value().isEmpty() ? clazz.getSimpleName() : dto.value());
			entityProperty.setEditable(dto.editable());
			entityProperty.setElementJsonList();
			entityProperty.setElements(entityElements);
			entityProperty.setDetailFieldName(fieldToShowDetail);
			entityProperty.setDateElementsJson(MyJsonUtil.listToJson(entityProperty.getDateElements()));
			entityProperty.setFieldNames(MyJsonUtil.listToJson(fieldNames));
			entityProperty.setFieldNameList(fieldNames);
			entityProperty.setFormInputColumn(dto.formInputColumn().value);
			entityProperty.determineIdField();

			log.info("============ENTITY PROPERTY: {} ", entityProperty);

			return entityProperty;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private Map<String, List<Field>> sortListByQuestionareSection(List<Field> fieldList) {
		Map<String, List<Field>> temp = MapUtil.singleMap(AdditionalQuestionField.DEFAULT_GROUP_NAME,
				new ArrayList<>());

		String key = AdditionalQuestionField.DEFAULT_GROUP_NAME;
		for (Field field : fieldList) {
			FormField formField = field.getAnnotation(FormField.class);
			boolean isIDField = field.getAnnotation(Id.class) != null
					|| field.getAnnotation(org.springframework.data.annotation.Id.class) != null;

			if (null == formField) {
				continue;
			}
			AdditionalQuestionField additionalQuestionField = field.getAnnotation(AdditionalQuestionField.class);
			if (null == additionalQuestionField || isIDField) {
				key = "OTHER";
				log.debug("{} has no additionalQuestionareField", field.getName());
			} else {
				key = additionalQuestionField.value();
			}
			if (temp.get(key) == null) {
				temp.put(key, new ArrayList<>());
			}
			temp.get(key).add(field);
			log.debug("{}: {}", key, field.getName());
		}
		log.debug("QUestionare Map: {}", temp);
		return (temp);

	}

	public static <T extends Annotation> T getClassAnnotation(Class<?> entityClass, Class<T> annotation) {
		try {
			return entityClass.getAnnotation(annotation);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * get fields of a class, accessible true
	 * 
	 * @param clazz
	 * @return
	 */
	public List<Field> getDeclaredFields() {
		Field[] baseField = clazz.getDeclaredFields();
//
//		List<EntityElement> entityElements = new ArrayList<EntityElement>();
		List<Field> fieldList = new ArrayList<>();

		for (Field field : baseField) {
			field.setAccessible(true);
			fieldList.add(field);
		}
		if (clazz.getSuperclass() != null) {

			Field[] parentFields = clazz.getSuperclass().getDeclaredFields();

			for (Field field : parentFields) {
				field.setAccessible(true);
				fieldList.add(field);
			}

		}
		return fieldList;
	}

}
