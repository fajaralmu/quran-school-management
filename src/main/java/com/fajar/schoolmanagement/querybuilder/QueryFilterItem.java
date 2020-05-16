package com.fajar.schoolmanagement.querybuilder;

import com.fajar.schoolmanagement.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public   class QueryFilterItem{ 
	private Object value;
	private boolean exacts;
	private String tableName;
	private String columnName;
	private String dateMode;
	
	private String getDoubledQuotedColumn() {
		String fullColumnName = "";
		if(tableName != null && !tableName.isEmpty() ) { 
			
			fullColumnName = StringUtil.buildTableColumnDoubleQuoted(tableName, columnName);
		}else {
		
			fullColumnName = StringUtil.doubleQuoteMysql(columnName);
		}
		if(dateMode != null && !dateMode.isEmpty() ) {
			fullColumnName = dateMode +"("+fullColumnName+")";
		}
		
		return fullColumnName;
	}
	
	public String generateSqlString() {
		String key = getDoubledQuotedColumn();
		StringBuilder sqlItem =  new StringBuilder(key);
		if (exacts) {
			sqlItem = sqlItem.append(" = '").append(value).append("' ");
		}else  {
			sqlItem = sqlItem.append(" LIKE '%").append(value).append("%' ");

		}   
		
		log.info("Generated sql item: {}", sqlItem);
		
		return sqlItem.toString();
	}
}