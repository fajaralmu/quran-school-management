package com.fajar.schoolmanagement.service.entity;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.repository.StudentRepository;

@Service
public class StudentUpdateService extends BaseEntityUpdateService{

	@Autowired
	private StudentRepository StudentRepository; 
	 
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		Student Student = (Student) copyNewElement(baseEntity, newRecord);
		String base64Image = Student.getImageUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage(baseEntity.getClass().getSimpleName(), base64Image);
				Student.setImageUrl(imageName);
			} catch (IOException e) {

				Student.setImageUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Student> dbStudent = StudentRepository.findById(Student.getId());
				if (dbStudent.isPresent()) {
					Student.setImageUrl(dbStudent.get().getImageUrl());
				}
			}
		}
		Student newStudent = StudentRepository.save(Student);
		return WebResponse.builder().entity(newStudent).build();
	}
}
