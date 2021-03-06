package com.fajar.schoolmanagement.entity.setting;

import java.io.Serializable;

import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.service.entity.BaseEntityUpdateService;
import com.fajar.schoolmanagement.service.entity.EntityUpdateInterceptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityManagementConfig implements Serializable {
	private static final long serialVersionUID = -3980738115751592524L;
	private long id;
	private Class<? extends BaseEntity> entityClass;
	private BaseEntityUpdateService entityUpdateService;
	private String fieldName; 

	public EntityManagementConfig(String fieldName, Class<? extends BaseEntity> entityClass, BaseEntityUpdateService service, EntityUpdateInterceptor updateInterceptor) {
		this.entityClass = entityClass; 
		this.entityUpdateService = service;
		if(null == fieldName) {
			fieldName = "entity";
		}
		this.fieldName = fieldName;
//		this.updateInterceptor = updateInterceptor;
	}

}
