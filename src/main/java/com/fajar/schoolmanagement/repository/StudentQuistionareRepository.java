package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.Capital;
import com.fajar.schoolmanagement.entity.StudentQuistionare;

public interface StudentQuistionareRepository extends JpaRepository<StudentQuistionare, Long> {

	List<Capital> findByDeletedFalse();

}
