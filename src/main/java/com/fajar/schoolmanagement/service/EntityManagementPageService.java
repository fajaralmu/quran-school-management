package com.fajar.schoolmanagement.service;

import static com.fajar.schoolmanagement.util.MvcUtil.constructCommonModel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.entity.setting.EntityManagementConfig;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.entity.setting.EntityPropertyBuilder;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.util.CollectionUtil;
import com.fajar.schoolmanagement.util.EntityUtil;
import com.fajar.schoolmanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityManagementPageService {

	@Autowired
	private EntityRepository entityRepository;
	@Autowired
	private MenuInitiationService menuInitiationService;

	public <T extends BaseEntity> Model setModel(HttpServletRequest request, Model model, Class<T > entityClass) throws Exception {
		return setModel(request, model, entityClass.getSimpleName().toLowerCase());
	}

	public Model setModel(HttpServletRequest request, Model model, String entityNameLowerCase) throws Exception {

		EntityManagementConfig entityConfig = entityRepository.getConfig(entityNameLowerCase);

		if (null == entityConfig) {
			throw new IllegalArgumentException("Invalid entity key!");
		}

		HashMap<String, List<?>> additionalListObject = getFixedListObjects(entityConfig.getEntityClass());
		EntityProperty entityProperty = new EntityPropertyBuilder(entityConfig.getEntityClass(), additionalListObject).createEntityProperty();
		model = constructCommonModel(request, entityProperty, model, entityConfig.getEntityClass().getSimpleName(),
				"management");

		String pageCode = getPageCode(entityConfig.getEntityClass(), request);
		model.addAttribute(SessionUtil.PAGE_CODE, pageCode);
		return model;
	}

	private HashMap<String, List<?>> getFixedListObjects(Class<? extends BaseEntity> entityClass) {
		log.info("getFixedListObjects: {}", entityClass);
		HashMap<String, List<?>> listObject = new HashMap<>();
		try {
			List<Field> fixedListFields = EntityUtil.getFixedListFields(entityClass);

			for (int i = 0; i < fixedListFields.size(); i++) {
				Field field = fixedListFields.get(i);
				Class<?> fieldType = field.getType();
				Class<? extends BaseEntity> type = EntityUtil.castObject(fieldType);
				List<? extends BaseEntity> list = entityRepository.findAll(type);
				listObject.put(field.getName(), CollectionUtil.convertList(list));

				log.info("add list with key: {}", field.getName());
			}

		} catch (Exception e) {

			log.error("Error adding fiexd list items");
			e.printStackTrace();
		}
		return listObject;
	}

	private String getPageCode(Class<? extends BaseEntity> entityClass, HttpServletRequest request) {
		try {
			Menu managementMenu = menuInitiationService.getMenuByCode(entityClass.getSimpleName().toLowerCase());
			return managementMenu.getMenuPage().getCode();
		} catch (Exception e) {
			log.info("getPageCode error");
			e.printStackTrace();
			return null;
		}
	}

}
