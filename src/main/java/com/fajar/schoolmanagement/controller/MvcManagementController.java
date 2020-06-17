package com.fajar.schoolmanagement.controller;

import static com.fajar.schoolmanagement.util.MvcUtil.constructCommonModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.entity.Profile;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.service.EntityManagementPageService;
import com.fajar.schoolmanagement.service.EntityService;
import com.fajar.schoolmanagement.util.CollectionUtil;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Slf4j
@Controller
@RequestMapping("management")
public class MvcManagementController extends BaseController {

	@Autowired
	private EntityService entityService;
	@Autowired
	private EntityManagementPageService entityManagementPageService;

	private static final String ERROR_404_PAGE = "error/notfound";

	public MvcManagementController() {
		log.info("-----------------Mvc Management Controller------------------");
	}

	@PostConstruct
	private void init() {
		basePage = webConfigService.getBasePage();
		LogProxyFactory.setLoggers(this);
	}

	@RequestMapping(value = { "/common/{name}" })
	public String unit(@PathVariable("name") String name, Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/common/" + name);
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		model = entityManagementPageService.setModel(request, model, name);
		
		return basePage;
	}

	@RequestMapping(value = { "/profile" })
	public String profile(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Profile.class, null);

		model = constructCommonModel(request, entityProperty, model, Profile.class.getSimpleName().toLowerCase(), "management");
		// override singleObject
		model.addAttribute("entityId", webConfigService.getProfile().getId());
		model.addAttribute("singleRecord", true);
		return basePage;
	}

	@RequestMapping(value = { "/menu" })
	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/menu");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, List<?>> listObject = new HashMap<>();
		List pages = componentService.getAllPages();
		log.debug("pages: {}", pages);
		listObject.put("menuPage", pages );
		EntityProperty entityProperty = EntityUtil.createEntityProperty(Menu.class, listObject);
		model = constructCommonModel(request, entityProperty, model, "Menu", "management");
		return basePage;
	}
	
	@RequestMapping(value = { "/fundflow" })
	public String fundflow(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/fundflow");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, List<?>> listObject = new HashMap<>();
		List fundTypes = componentService.getAllFundTypes();
		log.debug("fundTypes: {}", fundTypes);
		listObject.put("fundType", fundTypes );
		EntityProperty entityProperty = EntityUtil.createEntityProperty(CapitalFlow.class, listObject);
		model = constructCommonModel(request, entityProperty, model, "Fund Flow", "management");
		return basePage;
	}
	
	@RequestMapping(value = { "/costflow" })
	public String costflow(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/costflow");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, List<?>> listObject = new HashMap<>();
		List costTypes = componentService.getAllCostTypes();
		log.debug("costTypes: {}", costTypes);
		listObject.put("costType", costTypes );
		EntityProperty entityProperty = EntityUtil.createEntityProperty(CostFlow.class, listObject);
		model = constructCommonModel(request, entityProperty, model, "Cost Flow", "management");
		return basePage;
	}

	/** RESTRICTED ACCESS **/

//	@RequestMapping(value = { "/messages" })
//	public String messages(Model model, HttpServletRequest request, HttpServletResponse response)
//			throws IOException {
//
//		if (!userService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		try {
//			checkUserAccess(userService.getUserFromSession(request), "/management/messages");
//		} catch (Exception e) {
//			return ERROR_404_PAGE;
//		}
//		EntityProperty entityProperty = EntityUtil.createEntityProperty(Message.class, null); 
//		entityProperty.setEditable(false);
//		entityProperty.removeElements("color", "fontColor");
//		System.out.println("================ELEMENTS:"+MyJsonUtil.listToJson(entityProperty.getElements()));
//		model = constructCommonModel(request,entityProperty, model, "message", "management");
//		return basePage;
//	}

//	@RequestMapping(value = { "/productFlow" })
//	public String productflow(Model model, HttpServletRequest request, HttpServletResponse response)
//			throws IOException {
//
//		if (!userService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		try {
//			checkUserAccess(userService.getUserFromSession(request), "/management/productFlow");
//		} catch (Exception e) {
//			return ERROR_404_PAGE;
//		}
//		EntityProperty entityProperty = EntityUtil.createEntityProperty(ProductFlow.class, null); 
//		model = constructCommonModel(request,entityProperty, model, "productFlow", "management");
//		return basePage;
//	}

	@RequestMapping(value = { "/user" })
	public String user(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/user");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		HashMap<String, List<?>> listObject = new HashMap<>();
		listObject.put("userRole", CollectionUtil.convertList(entityService.getAllUserRole()));
		EntityProperty entityProperty = EntityUtil.createEntityProperty(User.class, listObject);
		model = constructCommonModel(request, entityProperty, model, "User", "management");
		return basePage;
	}

//	@RequestMapping(value = { "/menu" })
//	public String menu(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//		if (!userService.hasSession(request)) {
//			sendRedirectLogin(request, response);
//			return basePage;
//		}
//		try {
//			checkUserAccess(userService.getUserFromSession(request), "/management/menu");
//		} catch (Exception e) {
//			return ERROR_404_PAGE;
//		}
//		EntityProperty entityProperty = EntityUtil.createEntityProperty(Menu.class, null); 
//		model = constructCommonModel(request, entityProperty, model, "Menu", "management");
//		return basePage;
//	} 

	/**
	 * 
	 * NON ENTITY
	 * 
	 */

	@RequestMapping(value = { "/appsession" })
	public String appsession(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!userService.hasSession(request)) {
			sendRedirectLogin(request, response);
			return basePage;
		}
		try {
			checkUserAccess(userService.getUserFromSession(request), "/management/menu");
		} catch (Exception e) {
			return ERROR_404_PAGE;
		}
		model.addAttribute("title", "Apps Sessions");
		model.addAttribute("pageUrl", "shop/app-session");
		model.addAttribute("page", "management");
		return basePage;
	}

	private void checkUserAccess(User user, String url) throws Exception {
		componentService.checkAccess(user, url);
	}

	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("com.fajar.entity.costflow");
	}

}
