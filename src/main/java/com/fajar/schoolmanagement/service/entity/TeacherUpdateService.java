package com.fajar.schoolmanagement.service.entity;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Teacher;
import com.fajar.schoolmanagement.repository.TeacherRepository;

@Service
public class TeacherUpdateService extends BaseEntityUpdateService{

	@Autowired
	private TeacherRepository TeacherRepository; 
	 
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		Teacher Teacher = (Teacher) copyNewElement(baseEntity, newRecord);
		String base64Image = Teacher.getImageUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage(baseEntity.getClass().getSimpleName(), base64Image);
				Teacher.setImageUrl(imageName);
			} catch (IOException e) {

				Teacher.setImageUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Teacher> dbTeacher = TeacherRepository.findById(Teacher.getId());
				if (dbTeacher.isPresent()) {
					Teacher.setImageUrl(dbTeacher.get().getImageUrl());
				}
			}
		}
		Teacher newTeacher = TeacherRepository.save(Teacher);
		return WebResponse.builder().entity(newTeacher).build();
	}
}
