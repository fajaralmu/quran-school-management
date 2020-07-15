package com.fajar.schoolmanagement.service.entity;

import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.User;

@Service
public class UserUpdateService extends BaseEntityUpdateService<User> {

	@Override
	public WebResponse saveEntity(User baseEntity, boolean newRecord) {
		User user = copyNewElement(baseEntity, newRecord);
		User newUser = saveObject(user);
		return WebResponse.builder().entity(newUser).build();
	}
}
