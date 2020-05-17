package com.fajar.schoolmanagement.service.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SourceOfFund {

	DONATION_THRUSDAY("DONATION_THRUSDAY"), CASH_BALANCE("CASH_BALANCE"), DONATION_MONTHLY("DONATION_MONTHLY");

	@Deprecated
	public final String value;

	private SourceOfFund(String val) {
		value = val;
	}

	@JsonCreator
	public static SourceOfFund forValue(String value) {
		SourceOfFund[] enumKeys = SourceOfFund.values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].toString().equals(value)) {
				return enumKeys[i];
			}
		}

		return null;
	}

	@JsonValue
	public String toValue() {
		SourceOfFund[] enumKeys = SourceOfFund.values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].equals(this)) {
				return enumKeys[i].toString();
			}
		}

		return null; // or fail
	}
}
