package com.fajar.schoolmanagement.service;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.controller.MvcAdminController;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.entity.Page;
import com.fajar.schoolmanagement.repository.MenuRepository;
import com.fajar.schoolmanagement.repository.PageRepository;
import com.fajar.schoolmanagement.util.EntityUtil;
import com.fajar.schoolmanagement.util.MvcUtil;
import com.fajar.schoolmanagement.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MenuInitiationService {

	private static final String MANAGEMENT = "management";

	@Autowired
	private WebConfigService webConfigService;

	private List<Type> persistenceEntities;

	@Autowired
	private MenuRepository MenuRepository;

	@Autowired
	private PageRepository PageRepository;

	@PostConstruct
	public void init() {

		log.info("MenuInitiationService INITIALIZE");
		try {
			checkDefaultMenu();
			checkManagementPage();
			checkAdminMenus();
			getPersistenctEntities();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void checkAdminMenus() {
		Class<MvcAdminController> adminController = MvcAdminController.class;
		List<Method> methods = MvcUtil.getRequesMappingMethods(adminController);
		RequestMapping baseReqMapping = adminController.getAnnotation(RequestMapping.class);
		
		String baseMapping = baseReqMapping.value()[0];
		
		for (Method method : methods) {
			try {
				checkAdminMenu(baseMapping, method);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkAdminMenu(String baseMapping, Method method) { 
		String menuCode = method.getName().toLowerCase();

		if (MenuRepository.findByCode(menuCode) == null) {

			Menu adminMenu = constructAdminMenu(baseMapping, method);
			MenuRepository.save(adminMenu);
		}

	}

	private Menu constructAdminMenu(String baseMapping, Method method) {
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

		log.info("constructAdminMenu: {} url: /{}", method.getName(), requestMapping.value());

		Menu adminMenu = new Menu();
		adminMenu.setCode(method.getName().toLowerCase());
		adminMenu.setColor("#ffffff");
		adminMenu.setFontColor("#000000");
		adminMenu.setDescription(StringUtil.extractCamelCase(method.getName()));
		adminMenu.setName(StringUtil.extractCamelCase(method.getName()));
		adminMenu.setUrl("/"+baseMapping+"/" + requestMapping.value()[0]);
		adminMenu.setMenuPage(webConfigService.defaultAdminPage());

		return adminMenu;
	}

	private void checkManagementPage() {

		Page managementPage = getPageByCode(MANAGEMENT);
		if (null != managementPage) {
			log.info("managementPage FOUND");
			return;
		}

		log.info("managementPage NOT FOUND. WILL ADD SETTING");
		PageRepository.save(defaultManagementPage());
	}

	private Page defaultManagementPage() {

		return webConfigService.getDefaultManagementPage();
	}

	private void getPersistenctEntities() {

		this.persistenceEntities = webConfigService.getEntityClassess();
		for (Type type : persistenceEntities) {
			validateManagementPage(EntityUtil.castObject(type));

		}
	}

	private void validateManagementPage(Class<? extends BaseEntity> entityClass) {
		Dto dto = EntityUtil.getClassAnnotation(entityClass, Dto.class);
		if (null == dto)
			return;

		Menu managementMenu = getMenuByCode(entityClass.getSimpleName().toLowerCase());
		if (null == managementMenu) {
			addNewManagementMenuPageFor(entityClass);
		}

	}

	private void addNewManagementMenuPageFor(Class<? extends BaseEntity> entityClass) {
		log.info("Will add default menu for: {}", entityClass.getSimpleName());

		Dto dto = EntityUtil.getClassAnnotation(entityClass, Dto.class);
		if (null == dto) {
			return;
		}

		boolean commonPage = dto.commonManagementPage();
		String menuCode = entityClass.getSimpleName().toLowerCase();

		Menu menu = new Menu();
		menu.setCode(menuCode);
		menu.setName(StringUtil.extractCamelCase(entityClass.getSimpleName()) + " Management"); 
		Page menuPage = getPageByCode(MANAGEMENT);
		menu.setMenuPage(menuPage);
		menu.setColor("#ffffff");
		menu.setFontColor("#000000");
		menu.setDescription("Generated Management Page For: " + entityClass.getSimpleName());
		
		if (commonPage) {
			menu.setUrl("/management/common/" + menuCode);
		} else {
			menu.setUrl("/management/" + menuCode);
		}
		
		MenuRepository.save(menu);

		log.info("Success Adding Management Menu For: {}", menuCode);
	}

	private void checkDefaultMenu() {
		webConfigService.checkDefaultMenu();
	}

	public Menu getMenuByCode(String code) {
		return MenuRepository.findByCode(code);
	}

	private Page getPageByCode(String code) {
		return PageRepository.findByCode(code);
	}

	public void resetMenus() {

		log.info("Will reset menus");
		MenuRepository.deleteAll();
		log.info("ALl menus have been deleted");
		PageRepository.deleteAll();
		log.info("All pages have been deleted");
		
		webConfigService.init();
		this.init();
		
	}

}
