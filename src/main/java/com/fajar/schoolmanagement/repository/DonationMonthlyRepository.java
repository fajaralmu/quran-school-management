package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.schoolmanagement.entity.Cost;
import com.fajar.schoolmanagement.entity.DonationMonthly;

public interface DonationMonthlyRepository extends JpaRepository<DonationMonthly, Long> {

	List<Cost> findByDeletedFalse();

	@Query(nativeQuery = true, value = "select * from donation_montly where MONTH(`date`) = ?1 and YEAR(`date`) = ?2")
	List<DonationMonthly> findByMonthAndYear(int month, int year);
	@Query(nativeQuery = true, value = "select * from donation_montly where and YEAR(`date`) = ?1")
	List<DonationMonthly> findByYear(int year);

}
