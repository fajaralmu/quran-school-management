package com.fajar.schoolmanagement.service;

import static com.fajar.schoolmanagement.service.UserSessionService.ATTR_REQUEST_URI;
import static com.fajar.schoolmanagement.service.UserSessionService.HEADER_LOGIN_KEY;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.dto.RegistryModel;
import com.fajar.schoolmanagement.dto.WebRequest;
import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.entity.UserRole;
import com.fajar.schoolmanagement.repository.UserRepository;
import com.fajar.schoolmanagement.repository.UserRoleRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAccountService {
	
	
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private UserSessionService userSessionService;
//	@Autowired
//	private RegistryService registryService;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
//	/**
//	 * add new user
//	 * @param request
//	 * @return
//	 */
//	public WebResponse registerUser(WebRequest request) { 
//		WebResponse response  = new WebResponse();
//		Optional<UserRole> regularRoleOpt = userRoleRepository.findById(2L);
//		
//		if(regularRoleOpt.isPresent() == false) {
//			throw new RuntimeException("invalid role");
//		}
//		UserRole regularRole = regularRoleOpt.get();
//		
//		User user = new User();
//		user.setDisplayName(request.getUser().getDisplayName());
//		user.setDeleted(false);
//		user.setRole(regularRole);
//		user.setPassword(request.getUser().getPassword());
//		user.setUsername(request.getUser().getUsername());
//		
//		try {
//			User newUser = userRepository.save(user);
//			response.setUser(newUser);
//			return response;
//		}catch (Exception e) {
//			response.setCode("01");
//			response.setMessage("Error Record new Data");
//			return response;
//		}
//	}
	
	/**
	 * get user by username and password
	 * @param request
	 * @return
	 */
	public User getUser(WebRequest request) {
		User dbUser = userRepository.findByUsernameAndPassword(request.getUser().getUsername(), request.getUser().getPassword());
		return dbUser;
	}

	/**
	 * login to system
	 * @param request
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws IllegalAccessException
	 */
	public WebResponse login(WebRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IllegalAccessException {
		User dbUser = getUser(request);
		 
		if(dbUser == null) {
			return new WebResponse("01","invalid credential");
		} 
		 
		String loginKey = userSessionService.addUserSession(dbUser,httpRequest,httpResponse);
		dbUser.setLoginKey(loginKey); 
		dbUser.setPassword(null);
		dbUser.setRole(null); 
		
		WebResponse response = new WebResponse("00","success");   
		response.setEntity(dbUser);
		
		log.info("LOGIN SUCCESS");
		
		if(httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI) != null) {
			log.info("WILL REDIRECT TO REQUESTED URI: "+httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI));
			response.setRedirectUrl(httpRequest.getSession(false).getAttribute(ATTR_REQUEST_URI).toString());			
		}
		
		return response;
	} 
	
	/**
	 * logout from system
	 * @param httpRequest
	 * @return
	 */
	public boolean logout(HttpServletRequest httpRequest) { 
		 
		boolean logoutResult = userSessionService.logout(httpRequest);
		return logoutResult;
	} 

	/**
	 * validate session token & registry token
	 * @param httpRequest
	 * @return
	 */
	public boolean validateToken(HttpServletRequest httpRequest) {
		String requestToken = httpRequest.getHeader(UserSessionService.HEADER_REQUEST_TOKEN);
		/**
		 * TESTING
		 */
		boolean validated = userSessionService.validatePageRequest(httpRequest );
		if(validated) {
			return true;
		}
		
		if(requestToken == null) {
			log.info("NULL TOKEN");
			return false;
			
		}else {
			
			String existingToken = userSessionService.getToken(httpRequest);
			log.info("|| REQ_TOKEN: "+requestToken+" vs EXISTING:"+existingToken+"||");
			
			boolean tokenEquals = requestToken.equals(existingToken); 
			return tokenEquals;
		}
	}
 

}
