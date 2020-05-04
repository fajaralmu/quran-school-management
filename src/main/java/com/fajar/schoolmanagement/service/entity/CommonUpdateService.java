package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.repository.EntityRepository;

@Service
public class CommonUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord) {
		entity = (BaseEntity) copyNewElement(entity, newRecord);
		BaseEntity newEntity = entityRepository.save(entity);
		return WebResponse.builder().entity(newEntity).build();
	}
}
