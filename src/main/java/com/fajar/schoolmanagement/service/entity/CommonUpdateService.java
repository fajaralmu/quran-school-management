package com.fajar.schoolmanagement.service.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord) {
		log.info("saving entity: {}", entity.getClass());
		entity = (BaseEntity) copyNewElement(entity, newRecord);
		try {
			validateEntityFields(entity, newRecord);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BaseEntity newEntity = entityRepository.save(entity);
		return WebResponse.builder().entity(newEntity).build();
	}
	
	private void validateEntityFields(BaseEntity entity, boolean newRecord)  {
		log.info("validating entity: {} newRecord: {}", entity.getClass(), newRecord);
		BaseEntity existingEntity = null;
		if(!newRecord) {
			existingEntity = (BaseEntity)entityRepository.findById(entity.getClass(), entity.getId());
		}
		
		List<Field> fields = EntityUtil.getDeclaredFields(entity.getClass());
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i); 
			
			try {
			 
				FormField formfield = field.getAnnotation(FormField.class);
				if(null == formfield) {
					continue;
				} 
				
				Object fieldValue = field.get(entity);
				
				switch (formfield.type()) {
				case FIELD_TYPE_IMAGE:
					
					if(!newRecord && fieldValue == null && existingEntity != null) {
						Object existingImage = field.get(existingEntity);
						field.set(entity, existingImage);
					}else {
						String imageName = updateImage(field, entity);
						field.set(entity, imageName);
					}
					break;
	
				default:
					break;
				}
			}catch (Exception e) {
				log.error("Error validating field: {}", field.getName());
				e.printStackTrace();
			}
		}
	}
	
	private String updateImage(Field field, BaseEntity object) { 
		try {
			Object base64Value = field.get(object);
			if(null != base64Value && base64Value.toString().trim().isEmpty() == false) {
				String imageName = fileService.writeImage(object.getClass().getSimpleName(), base64Value.toString());
				
				return imageName;
			}
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
