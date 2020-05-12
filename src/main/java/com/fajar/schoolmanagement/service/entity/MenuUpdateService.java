package com.fajar.schoolmanagement.service.entity;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.repository.MenuRepository;

@Service
public class MenuUpdateService extends BaseEntityUpdateService{

	@Autowired
	private MenuRepository menuRepository; 
	 
	@Override
	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord) {
		Menu menu = (Menu) copyNewElement(baseEntity, newRecord); 
		
		
		String base64Image = menu.getIconUrl();
		if (base64Image != null && !base64Image.equals("")) {
			try {
				String imageName = fileService.writeImage(baseEntity.getClass().getSimpleName(), base64Image);
				menu.setIconUrl(imageName);
			} catch (IOException e) {

				menu.setIconUrl(null);
				e.printStackTrace();
			}
		} else {
			if (!newRecord) {
				Optional<Menu> dbMenu = menuRepository.findById(menu.getId());
				if (dbMenu.isPresent()) {
					menu.setIconUrl(dbMenu.get().getIconUrl());
				}
			}
		}
		
		if(menu.getUrl().startsWith("/") == false) {
			menu.setUrl("/"+menu.getUrl());
		}
		
		Menu newMenu = menuRepository.save(menu);
		return WebResponse.builder().entity(newMenu).build();
	}
}
