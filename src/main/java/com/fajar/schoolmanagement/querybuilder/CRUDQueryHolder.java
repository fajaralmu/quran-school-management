package com.fajar.schoolmanagement.querybuilder;

import static com.fajar.schoolmanagement.querybuilder.QueryUtil.SQL_KEYWORDSET_SELECT_COUNT;
import static com.fajar.schoolmanagement.querybuilder.QueryUtil.doubleQuoteMysql;
import static com.fajar.schoolmanagement.util.StringUtil.buildString;

import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.entity.BaseEntity;

public class CRUDQueryHolder extends QueryHolder {

	public CRUDQueryHolder(Class<? extends BaseEntity> _entityClass, Filter filter) {
		super(_entityClass, filter);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3853137211018712818L;

	@Override
	public void buildSqlSelectAndSingleResult() {

		whereClauseSql = "";
		joinSql = QueryUtil.createLeftJoinQueryFullObject(entityClass);

		if (this.withFilteredField) {
			whereClauseSql = QueryUtil.createWhereClause(entityClass, filter.getFieldsFilter(),
//					contains, 
					exacts);
		}

	}

	@Override
	public String getSqlSelect() {

		String orderSQL = withOrder ? QueryUtil.orderSQL(entityClass, orderType, orderBy) : "";
		String limitOffsetSQL = "";
		if (this.withLimit) {
			limitOffsetSQL = buildString(QueryUtil.SQL_KEYWORD_LIMIT, String.valueOf(filter.getLimit()),
					QueryUtil.SQL_KEYWORD_OFFSET, String.valueOf(offset));
		}

		String sqlSelect = buildString(selectFromTable(), joinSql, whereClauseSql, orderSQL, limitOffsetSQL);

		return sqlSelect;
	}

	@Override
	public String getSqlSingleResult() {

		String sqlSelectCount = buildString(SQL_KEYWORDSET_SELECT_COUNT, doubleQuoteMysql(tableName), joinSql,
				whereClauseSql);
		;
		return sqlSelectCount;
	}
}
