package com.fajar.schoolmanagement.dto;

import java.io.Serializable;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.ShopProfile;

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
	 
	private ShopProfile shopprofile;
	 
	/**
	 * ==========end entity============
	 */

	private Filter filter;
 
	private BaseEntity entityObject;
	
	private String destination;
	private String username;
	private String value;
	
	 

}
