package com.fajar.schoolmanagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.User;

public interface UserRepository extends JpaRepository< User	, Long>{

	User findByUsernameAndPassword(String username, String password);
	
}