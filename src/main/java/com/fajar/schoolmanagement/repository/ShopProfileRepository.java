package com.fajar.schoolmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.SchoolProfile;

public interface ShopProfileRepository extends JpaRepository<SchoolProfile, Long>{

	SchoolProfile findByMartCode(String martCode);

}
