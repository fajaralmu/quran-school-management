package com.fajar.schoolmanagement.service.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonUpdateService extends BaseEntityUpdateService<BaseEntity> {

	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord) {
		log.info("saving entity: {}", entity.getClass());
		entity = copyNewElement(entity, newRecord);

		
		validateEntityFields(entity, newRecord);
		interceptPreUpdate(entity);
		BaseEntity newEntity = entityRepository.save(entity);
		if(null == newEntity) {
			return WebResponse.failed("Operation Failed");
		}
		return WebResponse.builder().entity(newEntity).build();
	}

	/**
	 * execute things before persisting
	 * 
	 * @param entity
	 * @param updateInterceptor
	 */
	private void interceptPreUpdate(BaseEntity entity ) {
		EntityUpdateInterceptor<BaseEntity> updateInterceptor = entity.getUpdateInterceptor();
		if (null != updateInterceptor && null != entity) {
			log.info("Pre Update {}", entity.getClass().getSimpleName());
			
			try {
				updateInterceptor.preUpdate(entity);
//				if(entity instanceof Student) {
//					log.info("IMG: {}", ((Student) entity).getImageUrl());
//				}
				log.info("success pre update: {}", entity );
			} catch (Exception e) {

				log.error("Error pre update entity");
				e.printStackTrace();
			}
		}
	}

	/**
	 * validate object properties' value
	 * 
	 * @param entity
	 * @param newRecord
	 */
	private void validateEntityFields(BaseEntity entity, boolean newRecord) {
		log.info("validating entity: {} newRecord: {}", entity.getClass(), newRecord);
		try {

			BaseEntity existingEntity = null;
			if (!newRecord) {
				existingEntity = (BaseEntity) entityRepository.findById(entity.getClass(), entity.getId());
			}

			List<Field> fields = EntityUtil.getDeclaredFields(entity.getClass());
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);

				try {

					FormField formfield = field.getAnnotation(FormField.class);
					if (null == formfield) {
						continue;
					}

					Object fieldValue = field.get(entity);

					switch (formfield.type()) {
					case FIELD_TYPE_IMAGE:
						log.info("validate FIELD_TYPE_IMAGE");
						
						if (newRecord == false && fieldValue == null && existingEntity != null) {
							
							log.info("not newRecord and fieldValue is null, set to old value" );
							Object existingImage = field.get(existingEntity);
							field.set(entity, existingImage);
						} else {
							
							String imageName = updateImage(field, entity);
							log.info("imageName: {}",imageName );
							field.set(entity, imageName);
						}
						break;

					default:
						break;
					}
					log.info("validated field: {}", field.getName());
				} catch (Exception e) {
					
					log.error("Error validating field: {}", field.getName());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			
			log.error("Error validating entity {}", entity.getClass().getSimpleName());
			e.printStackTrace();
		}
	}

	/**
	 * update image field, writing to disc
	 * 
	 * @param field
	 * @param object
	 * @return
	 */
	private String updateImage(Field field, BaseEntity object) {
		try {
			Object base64Value = field.get(object);
			return writeImage(object, base64Value);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			e.printStackTrace();
		}
		return null;
	}

	private String writeImage(BaseEntity object, Object base64Value) {
		String fileName = null;
		if (null != base64Value && base64Value.toString().trim().isEmpty() == false) {
			try {
				fileName = fileService.writeImage(object.getClass().getSimpleName(), base64Value.toString());
			} catch (IOException e) {
				log.error("Error writing image for {}", object.getClass().getSimpleName());
				e.printStackTrace();
			}
		}
		return fileName;
	}
}
