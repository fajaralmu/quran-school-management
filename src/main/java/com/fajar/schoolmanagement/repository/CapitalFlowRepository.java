package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.schoolmanagement.entity.CapitalFlow;

public interface CapitalFlowRepository extends JpaRepository<CapitalFlow, Long> {

	List<CapitalFlow> findByDeletedFalse();

	@Query(nativeQuery = true, value = "select * from fund_flow  " + " where month(`date`) = ?1 and year(`date`) = ?2")
	List<CapitalFlow> findByPeriod(int month, int year);

}
