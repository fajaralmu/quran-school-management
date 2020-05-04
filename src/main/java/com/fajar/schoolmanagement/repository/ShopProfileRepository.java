package com.fajar.schoolmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.ShopProfile;

public interface ShopProfileRepository extends JpaRepository<ShopProfile, Long>{

	ShopProfile findByMartCode(String martCode);

}
