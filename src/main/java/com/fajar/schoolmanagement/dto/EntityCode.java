package com.fajar.schoolmanagement.dto;

public enum EntityCode {

	Capital("capital"),
	CapitalFlow("capitalflow"),
	CashBalance("cashbalance"),
	Cost("cost"),
	CostFlow("costflow"),
	Menu("menu"),
	Page("page"),
	Profile("profile"),
	RegisteredRequest("registeredrequest"),
	Student("student"),
	StudentParent("studentparent"),
	Teacher("teacher"),
	User("user"),
	UserRole("userrole");
	public final String code;
	private EntityCode(String code) {
		this.code = code;
	}
}
