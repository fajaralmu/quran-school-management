package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.schoolmanagement.entity.CostFlow;

public interface CostFlowRepository extends JpaRepository<CostFlow, Long> {

	@Query(nativeQuery = true, value = "select * from school_cost_flow where month(`date`) = ?1 and year(`date`) = ?2")
	List<CostFlow> findByPeriod(int month, int year);

	/**
	 * 
	 * @param sourceOfFundToString enum SourceOfFund.toString()
	 * @param year
	 * @return
	 */
	@Query(value = "select * from school_cost_flow where year(`date`) = ?2 and source_of_fund = ?1", nativeQuery = true)
	List<CostFlow> findBySourceOfFundAndYear(String sourceOfFundToString, int year);

}
