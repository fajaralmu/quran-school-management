package com.fajar.schoolmanagement.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.entity.Page;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.repository.MenuRepository;
import com.fajar.schoolmanagement.repository.PageRepository;
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

	public List<Page> getPages(HttpServletRequest request){
		
		boolean hasSession = userSessionService.hasSession(request);
		
		if(hasSession) {
			List<Page> pages = pageRepository.findAll();
			
			if(pages == null || pages.size() == 0) {
				
				Page page = defaultSettingPage();
				
				final Page savedPage = pageRepository.save(page);
				return new ArrayList<Page>() { 
					private static final long serialVersionUID = 1L;

					{
						add(savedPage);
					}
				};
			}
			
			return pages;
		}
		else
			return pageRepository.findByAuthorized(0);
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
	 * @param request
	 * @return
	 */
	public String getPageCode(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String link = uri.replace(request.getContextPath(), "");
		
		log.info("link: {}", link);
		Page page = pageRepository.findTop1ByLink(link);
		
		log.info("page from db : {}", page);
		if(null == page) {
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
		
		if(null == page) {
			throw new NotFoundException("Page Not Found");
		}
		
		if (page.getAuthorized() == 1 && !userSessionService.hasSession(request)) {
			
			return null;
		}
		
		List<Menu> menus = getMenuByPageCode(code);
		page.setMenus(menus );
		return page;
	}

	public List<Menu > getMenuByPageCode(String pageCode){
		
		List<Menu> menus = menuRepository.findByMenuPage_code(pageCode);
		
		if(menus == null || menus.size() == 0) {
			
			if(pageCode.equals(SETTING)) {
				Menu menu = defaultMenu();
				final Menu savedMenu = menuRepository.save(menu);
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
		menu.setMenuPage(menuPage );
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

	private List<Menu> getAvailableMenusForUser(User user, List<Menu> menus) {
		List<Menu> newMenus = new ArrayList<>();
		
//		for (Menu menu : menus) {
//			String[] menuAccess = menu.getPage().split("-");
//			if (menuAccess.length <= 1) {
//				newMenus.add(menu);
//				continue;
//			} else if (hasAccess(user, menuAccess[1])) {
//				newMenus.add(menu);
//				continue;
//			}
//
//		}
		return menus;
	}

	  

	public void checkAccess(User user, String url) throws Exception {
		Menu menu = menuRepository.findTop1ByUrl(url);
		if (menu == null) {
			throw new Exception("Not Found");

		}
		String[] menuAccess = {};// menu.getPage().split("-");
		if (menuAccess.length > 1) {
			String access 			= menuAccess[1];
			String[] userAccesses 	= user.getRole().getAccess().split(",");
			boolean hasAccess 		= hasAccess(user, access);
			if (!hasAccess) {
				throw new Exception("Has No Access");
			}
		}

	}

	

}
