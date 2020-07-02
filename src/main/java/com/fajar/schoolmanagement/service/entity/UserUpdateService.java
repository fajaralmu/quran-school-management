package com.fajar.schoolmanagement.service.entity;

import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.User;

@Service
public class UserUpdateService extends BaseEntityUpdateService{
 
	
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord, EntityUpdateInterceptor updateInterceptor) {
		User user = (User) copyNewElement(baseEntity, newRecord);
		User newUser = saveObject(user);
		return WebResponse.builder().entity(newUser).build();
	}
}
