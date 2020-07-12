package com.fajar.schoolmanagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.UserRole;

public interface UserRoleRepository extends JpaRepository< UserRole	, Long>{

	UserRole findByCode(String code);
	
}