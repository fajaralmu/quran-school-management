package com.fajar.schoolmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long>{

}
