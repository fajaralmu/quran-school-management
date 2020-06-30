package com.fajar.schoolmanagement.service;

import static com.fajar.schoolmanagement.util.MvcUtil.constructCommonModel;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fajar.schoolmanagement.entity.setting.EntityManagementConfig;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.util.EntityUtil;
import com.fajar.schoolmanagement.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EntityManagementPageService {
	 
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private EntityRepository entityRepository; 
	
	public Model setModel(HttpServletRequest request, Model model, String key) throws Exception {
		
		EntityManagementConfig entityConfig = entityRepository.getConfig(key);
		
		if(null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key!");
		}
		
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityConfig.getEntityClass(), null); 
		String entityName = entityConfig.getEntityClass().getSimpleName();
		String title = StringUtil.extractCamelCase(entityName);
		
		model = constructCommonModel(request, entityProperty, model, title, "management");
		try {
			model.addAttribute("entityPropJson", objectMapper.writeValueAsString(entityProperty));
		} catch (JsonProcessingException e) {
			 
			e.printStackTrace();
		}
		return model;
	}

}
