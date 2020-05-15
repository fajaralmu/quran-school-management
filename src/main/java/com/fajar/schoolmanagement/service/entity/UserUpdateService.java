package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.repository.UserRepository;

@Service
public class UserUpdateService extends BaseEntityUpdateService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord, EntityUpdate updateInterceptor) {
		User user = (User) copyNewElement(baseEntity, newRecord);
		User newUser = userRepository.save(user);
		return WebResponse.builder().entity(newUser).build();
	}
}
