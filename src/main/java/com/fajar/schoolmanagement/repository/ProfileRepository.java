package com.fajar.schoolmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>{

	Profile findByMartCode(String martCode);

}
