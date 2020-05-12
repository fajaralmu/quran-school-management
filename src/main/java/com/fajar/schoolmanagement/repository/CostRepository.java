package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.Cost;

public interface CostRepository extends JpaRepository<Cost, Long> {

	List<Cost> findByDeletedFalse();

}
