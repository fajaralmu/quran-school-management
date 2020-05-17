package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.schoolmanagement.entity.Cost;
import com.fajar.schoolmanagement.entity.DonationThursday;

public interface DonationThursdayRepository extends JpaRepository<DonationThursday, Long> {

	List<Cost> findByDeletedFalse();

	@Query(nativeQuery = true, value = "select * from donation_thrusday where MONTH(`date`) = ?1 and YEAR(`date`) = ?2")
	List<DonationThursday> findByMonthAndYear(int month, int year);

	@Query(nativeQuery = true, value = "select * from donation_thrusday where YEAR(`date`) = ?1") 
	List<DonationThursday> findByYear(int year);
}
