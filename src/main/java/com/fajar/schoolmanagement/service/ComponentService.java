package com.fajar.schoolmanagement.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebRequest;
import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Capital;
import com.fajar.schoolmanagement.entity.Cost;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.entity.Page;
import com.fajar.schoolmanagement.entity.Sequenced;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.entity.setting.EntityManagementConfig;
import com.fajar.schoolmanagement.repository.CapitalRepository;
import com.fajar.schoolmanagement.repository.CostRepository;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.repository.MenuRepository;
import com.fajar.schoolmanagement.repository.PageRepository;
import com.fajar.schoolmanagement.util.CollectionUtil;
import com.fajar.schoolmanagement.util.EntityUtil;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ComponentService {

	private static final String SETTING = "setting";
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private UserSessionService userSessionService;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private CostRepository costRepository;
	@Autowired
	private CapitalRepository capitalRepository;
	@Autowired
	private EntityRepository entityRepository;

	public List<Page> getPages(HttpServletRequest request) {

		boolean hasSession = userSessionService.hasSession(request);

		if (hasSession) {
			List<Page> pages = pageRepository.findByOrderBySequenceAsc();

			if (pages == null || pages.size() == 0) {

				Page page = defaultSettingPage();

				final Page savedPage = entityRepository.save(page);
				return CollectionUtil.listOf(savedPage);
			}

			return pages;
		} else
			return pageRepository.findByAuthorizedOrderBySequenceAsc(0);
	}

	private Page defaultSettingPage() {
		Page page = new Page();
		page.setName("Setting");
		page.setCode(SETTING);
		page.setAuthorized(1);
		page.setDescription("Default App Setting");
		page.setLink("/webapp/page/setting");
		page.setNonMenuPage(0);
		return page;
	}

	/**
	 * get page code
	 * 
	 * @param request
	 * @return
	 */
	public String getPageCode(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String link = uri.replace(request.getContextPath(), "");

		log.info("link: {}", link);
		Page page = pageRepository.findTop1ByLink(link);

		log.info("page from db : {}", page);
		if (null == page) {
			return "";
		}

		log.info("page code found: {}", page.getCode());
		return page.getCode();
	}

	public List<Page> getAllPages() {
		return pageRepository.findAll();
	}

	public Page getPage(String code, HttpServletRequest request) throws NotFoundException {
		Page page = pageRepository.findByCode(code);

		if (null == page) {
			throw new NotFoundException("Page Not Found");
		}

		if (page.getAuthorized() == 1 && !userSessionService.hasSession(request)) {

			return null;
		}

		List<Menu> menus = getMenuListByPageCode(code);
		page.setMenus(menus);
		return page;
	}

	public List<Menu> getMenuListByPageCode(String pageCode) {

		List<Menu> menus = menuRepository.findByMenuPage_code(pageCode);

		if (menus == null || menus.size() == 0) {

			if (pageCode.equals(SETTING)) {
				Menu menu = defaultMenu();
				final Menu savedMenu = entityRepository.save(menu);
				return new ArrayList<Menu>() {
					private static final long serialVersionUID = -6867018433722897471L;

					{
						add(savedMenu);
					}
				};
			}
		}

		EntityUtil.validateDefaultValues(menus);
		return menus;
	}

	private Menu defaultMenu() {
		Menu menu = new Menu();
		menu.setCode("000");
		menu.setName("Menu Management");
		menu.setUrl("/management/menu");
		Page menuPage = pageRepository.findByCode(SETTING);
		menu.setMenuPage(menuPage);
		return menu;
	}

	private boolean hasAccess(User user, String menuAccess) {
		boolean hasAccess = false;

		for (String userAccess : user.getRole().getAccess().split(",")) {
			if (userAccess.equals(menuAccess)) {
				hasAccess = true;
				break;
			}
		}

		return hasAccess;
	}

	public void checkAccess(User user, String url) throws Exception {
		Menu menu = menuRepository.findTop1ByUrl(url);
		if (menu == null) {
			throw new Exception("Not Found");

		}
		String[] menuAccess = {};// menu.getPage().split("-");
		if (menuAccess.length > 1) {
			String access = menuAccess[1];
//			String[] userAccesses = user.getRole().getAccess().split(",");
			boolean hasAccess = hasAccess(user, access);
			if (!hasAccess) {
				throw new Exception("Has No Access");
			}
		}

	}

	public List<Cost> getAllCostTypes() {
		return costRepository.findByDeletedFalse();
	}

	public List<Capital> getAllFundTypes() {
		return capitalRepository.findByDeletedFalse();
	}

	public WebResponse getMenuByPageCode(String pageCode) {

		List<Menu> menus = getMenuListByPageCode(pageCode);

		return WebResponse.builder().entities(CollectionUtil.convertList(menus)).build();
	}
  
	public WebResponse saveEntitySequence(WebRequest request, String entityName) {

		List<BaseEntity> orderedEntities = request.getOrderedEntities();
		EntityManagementConfig entityConfig = entityRepository.getConfig(entityName);
		Class<? extends BaseEntity> cls = entityConfig.getEntityClass();
		try {

			for (int i = 0; i < orderedEntities.size(); i++) {
				BaseEntity page = orderedEntities.get(i);
				updateSequence(i, page.getId(), cls);
			}

			WebResponse response = WebResponse.success();
			return response;

		} catch (Exception e) {
			log.error("Error saving page sequence");
			e.printStackTrace();
			return WebResponse.failed(e.getMessage());
		}
	}

	private void updateSequence(int sequence, Long id, Class<? extends BaseEntity> cls) {
		
		final BaseEntity dbRecord = entityRepository.findById(cls, id);
		if (dbRecord != null) {
			 
			((Sequenced)dbRecord).setSequence(sequence);
			entityRepository.save(dbRecord);
		}
	} 

}
