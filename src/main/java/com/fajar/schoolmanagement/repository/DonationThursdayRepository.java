package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.Cost;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.DonationThursday;

public interface DonationThursdayRepository extends JpaRepository<DonationThursday, Long> {

	List<Cost> findByDeletedFalse();

}
