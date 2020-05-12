package com.fajar.schoolmanagement.dto;

public enum EntityCode {

	Capital(""),
	CapitalFlow(""),
	CashBalance(""),
	Cost(""),
	CostFlow(""),
	Menu(""),
	Page(""),
	Profile(""),
	RegisteredRequest(""),
	Student(""),
	StudentParent(""),
	Teacher(""),
	User(""),
	UserRole("");
	public final String code;
	private EntityCode(String code) {
		this.code = code;
	}
}
