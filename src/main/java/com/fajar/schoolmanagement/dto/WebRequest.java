package com.fajar.schoolmanagement.dto;

import java.io.Serializable;

import org.apache.commons.math3.stat.descriptive.summary.Product;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Capital;
import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.entity.Cost;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.DonationThursday;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.entity.Page;
import com.fajar.schoolmanagement.entity.RegisteredRequest;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.entity.StudentParent;
import com.fajar.schoolmanagement.entity.Teacher;
import com.fajar.schoolmanagement.entity.Profile;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.entity.UserRole;

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
	 * ==========================================
	 *         ENTITY CRUD use lowerCase!!!
	 */

	private String entity;
	 
	private Profile profile;
	private RegisteredRequest registeredRequest;
	private User user;   
	private Menu menu; 
	private Product product;
	private UserRole userrole;    
	private Page page;
	private StudentParent studentparent;
	private Student student;
	private Teacher teacher;
	private Cost cost;
	private Capital capital;
	private CostFlow costflow;
	private CapitalFlow capitalflow;
	private DonationMonthly donationmonthly;
	private DonationThursday donationthursday;
	 
	/**
	 * ==========end entity============
	 */

	private Filter filter;
 
	private BaseEntity entityObject;
	
	private String destination;
	private String username;
	private String value;
	
	 

}
