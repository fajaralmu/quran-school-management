package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.entity.DonationOrphan;

public interface DonationOrphanRepository extends JpaRepository<DonationOrphan, Long> {

	@Query(nativeQuery = true, value = "select * from school_orphan_donation where month(`date`) = ?1 and year(`date`) = ?2")
	List<CostFlow> findByPeriod(int month, int year);

	 /**
	  * 
	  * @param cashflowType OrphanCashflowType.toString()
	  * @param year
	  * @return
	  */
	@Query(value = "select * from school_orphan_donation where year(`date`) = ?2 and cashflow_type = ?1", nativeQuery = true)
	List<CostFlow> findByCashflowTypeAndYear(String cashflowType, int year);

}
