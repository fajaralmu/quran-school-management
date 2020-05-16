package com.fajar.schoolmanagement.querybuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryHolder {
	private String sqlSelect;
	private String sqlSelectCount;

}
