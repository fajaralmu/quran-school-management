package com.fajar.schoolmanagement.dto;

import java.io.Serializable;
import java.util.HashMap;

import com.fajar.schoolmanagement.entity.User;

 
public class UserSessionModel implements Serializable  {
	public UserSessionModel()   { 
		super(); 
	}
	public UserSessionModel(User dbUser, String generateUserToken) {
		this.user = dbUser;
		this.userToken = generateUserToken;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3868645032944633878L;
	/**
	 * 
	 */
	//private static final long serialVersionUID = 3868645032944633878L;
	private User user;
	private HashMap<String, Object> tokens;
	private String userToken;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public HashMap<String, Object> getTokens() {
		return tokens;
	}
	public void setTokens(HashMap<String, Object> tokens) {
		this.tokens = tokens;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
