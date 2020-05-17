package com.fajar.schoolmanagement.financialjournal;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SourceOfFund {

	DONATION_THRUSDAY("DONATION_THRUSDAY"), CASH_BALANCE("CASH_BALANCE");

	public final String value;

	private SourceOfFund(String val) {
		value = val;
	}

	@JsonCreator
	public static SourceOfFund forValue(String value) {
		SourceOfFund[] enumKeys = SourceOfFund.values();
		for (int i = 0; i < enumKeys.length; i++) {
			if (enumKeys[i].value.equals(value)) {
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
				return enumKeys[i].value;
			}
		}

		return null; // or fail
	}
}
