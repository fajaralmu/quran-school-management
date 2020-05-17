package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.Cost;
import com.fajar.schoolmanagement.entity.DonationMonthly;

public interface DonationMonthlyRepository extends JpaRepository<DonationMonthly, Long> {

	List<Cost> findByDeletedFalse();

}
