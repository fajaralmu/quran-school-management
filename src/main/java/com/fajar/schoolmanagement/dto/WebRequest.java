package com.fajar.schoolmanagement.dto;

import java.io.Serializable;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.RegisteredRequest;
import com.fajar.schoolmanagement.entity.SchoolProfile;
import com.fajar.schoolmanagement.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 110411933791444017L;


	
	/**
	 * ENTITY CRUD use lowerCase!!!
	 */

	private String entity;
	 
	private SchoolProfile shopprofile;
	private RegisteredRequest registeredRequest;
	private User user;
	 
	/**
	 * ==========end entity============
	 */

	private Filter filter;
 
	private BaseEntity entityObject;
	
	private String destination;
	private String username;
	private String value;
	
	 

}
