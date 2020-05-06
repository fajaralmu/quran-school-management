package com.fajar.schoolmanagement.service.entity;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Profile;
import com.fajar.schoolmanagement.repository.ProfileRepository;

@Service
public class ProfileUpdateService extends BaseEntityUpdateService{

	@Autowired
	private ProfileRepository SchoolProfileRepository;
	
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		Profile SchoolProfile = (Profile) copyNewElement(baseEntity, newRecord);
		String base64Image = SchoolProfile.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage(baseEntity.getClass().getSimpleName(), base64Image);
				SchoolProfile.setIconUrl(imageName);
			} catch (IOException e) {

				SchoolProfile.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Profile> dbSchoolProfile = SchoolProfileRepository.findById(SchoolProfile.getId());
				if (dbSchoolProfile.isPresent()) {
					SchoolProfile.setIconUrl(dbSchoolProfile.get().getIconUrl());
				}
			}
		}
		Profile newSchoolProfile = SchoolProfileRepository.save(SchoolProfile);
		return WebResponse.builder().entity(newSchoolProfile).build();
	}
	
}

