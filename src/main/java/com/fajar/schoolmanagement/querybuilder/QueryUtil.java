package com.fajar.schoolmanagement.querybuilder;

import static com.fajar.schoolmanagement.util.EntityUtil.getDeclaredField;
import static com.fajar.schoolmanagement.util.StringUtil.buildString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.fajar.schoolmanagement.annotation.CustomEntity;
import com.fajar.schoolmanagement.annotation.FormField;
import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.dto.KeyValue;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.util.EntityUtil;
import com.fajar.schoolmanagement.util.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryUtil {
	
	
	private static final String DAY_SUFFIX = "-day";
	private static final String MONTH_SUFFIX = "-month";
	private static final String YEAR_SUFFIX = "-year";
	private static final String FILTER_DATE_DAY = "DAY";
	private static final String FILTER_DATE_MON1TH = "MONTH";
	private static final String FILTER_DATE_YEAR = "YEAR";
	
	//placeholders
	private static final String SQL_RAW_DATE_FILTER = " ${MODE}(`${TABLE_NAME}`.`${COLUMN_NAME}`) = ${VALUE} ";
	private static final String PLACEHOLDER_SQL_FOREIGN_ID = "${FOREIGN_ID}";
	private static final String PLACEHOLDER_SQL_JOIN_TABLE = "${JOIN_TABLE}";
	private static final String PLACEHOLDER_SQL_ENTITY_TABLE = "${ENTITY_TABLE}";
	private static final String PLACEHOLDER_SQL_JOIN_ID = "${JOIN_ID}";
	private static final String PLACEHOLDER_SQL_RAW_JOIN_STATEMENT = " LEFT JOIN `${JOIN_TABLE}` ON  `${JOIN_TABLE}`.`${JOIN_ID}` = `${ENTITY_TABLE}`.`${FOREIGN_ID}` ";
	private static final String PLACEHOLDER_SQL_TABLE_NAME = "${TABLE_NAME}";
	private static final String PLACEHOLDER_SQL_MODE = "${MODE}";
	private static final String PLACEHOLDER_SQL_COLUMN_NAME = "${COLUMN_NAME}";
	private static final String PLACEHOLDER_SQL_VALUE = "${VALUE}";
	
	private static final String SQL_KEYWORDSET_SELECT_COUNT = " SELECT COUNT(*) from  ";
	private static final String SQL_KEYWORD_SELECT = " SELECT "; 
	private static final String SQL_KEYWORD_LIMIT = " LIMIT ";
	private static final String SQL_KEYWORD_OFFSET = " OFFSET ";
	private static final String SQL_KEYWORD_ORDERBY = " ORDER BY ";
	private static final String SQL_KEYWORD_AND = " AND ";
	private static final String SQL_KEYWORD_WHERE = " WHERE ";
	private static final String SQL_KEYWORD_FROM = " from ";
	private static final String TABLE_NAME = "table_name_key";
	
	public static Field getFieldByName(String name, List<Field> fields) {
		return EntityUtil.getObjectFromListByFieldName("name", name, fields);
	}

	public static String getColumnName(Field field) {
		log.info("get column Name " + field.getDeclaringClass() + " from " + field.getName());

		if (field.getAnnotation(Column.class) == null)
			return field.getName();
		String columnName = ((Column) field.getAnnotation(Column.class)).name();
		if (columnName == null || columnName.equals("")) {
			columnName = field.getName();
		}
		return columnName;
	}

	
	/**
	 * create LEFT JOIN Statement for one field only
	 * @param entityClass
	 * @param field
	 * @return
	 */
	public static String createLeftJoinSql(Class entityClass, Field field) { 
		log.info("Create item sql left join: " + entityClass + ", field: " + field);

		JoinColumn joinColumn = EntityUtil.getFieldAnnotation(field, JoinColumn.class);

		if (null == joinColumn) {
			return "";
		}
 
		Class fieldClass 		= field.getType();
		Field idForeignField 	= EntityUtil.getIdField(fieldClass);

		String joinTableName 	= getTableName(fieldClass);
		String tableName 		= getTableName(entityClass);
		String foreignID 		= joinColumn.name();


		String sqlItem = PLACEHOLDER_SQL_RAW_JOIN_STATEMENT.
				replace(PLACEHOLDER_SQL_FOREIGN_ID, foreignID).
				replace(PLACEHOLDER_SQL_JOIN_TABLE, joinTableName).
				replace(PLACEHOLDER_SQL_ENTITY_TABLE, tableName).
				replace(PLACEHOLDER_SQL_JOIN_ID, getColumnName(idForeignField));

		return sqlItem;

	}

	/**
	 * create LEFT JOIN statement full object
	 * @param entityClass
	 * @return
	 */
	private static String createLeftJoinSQL(Class<? extends BaseEntity> entityClass) {

		StringBuilder sql = new StringBuilder("");

		CustomEntity customModel = EntityUtil.getClassAnnotation(entityClass, CustomEntity.class);

		List<Field> fields = EntityUtil.getDeclaredFields(entityClass);
		for (Field field : fields) {

			if (customModel != null
					) {//&& EntityUtil.existInList(field.getName(), Arrays.asList(customModel.rootFilter()))) {
				continue;
			}

			JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
			if (joinColumn != null) {

				String sqlItem = createLeftJoinSql(entityClass, field);

				sql = sql.append(sqlItem);

			}
		}

		if (customModel != null) {//&& customModel.rootFilter().length > 0) {
			sql = sql.append(validateRootFilter(entityClass, new String[] {}));// customModel.rootFilter()));
		}

		return sql.toString();
	}

	/**
	 * add join clause if class has root filter
	 * @param entityClass
	 * @param rootFilter
	 * @return
	 */
	public static String validateRootFilter(Class entityClass, String[] rootFilter) {

		StringBuilder stringBuilder = new StringBuilder("");

		Class currentType = entityClass;
		Field currentField = null;

		for (String string : rootFilter) {

			try {
				currentField = currentType.getDeclaredField(string);

				String sqlJoinItem = createLeftJoinSql(currentType, currentField);
				stringBuilder = stringBuilder.append(sqlJoinItem);

				currentType = currentField.getType();

			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}

		}

		return stringBuilder.toString();
	}

	private static String createWhereClause(Class entityClass, Map<String, Object> filter,  
		 final	boolean allItemExactSearch ) {

		String tableName 						= getTableName(entityClass);
		List<QueryFilterItem> sqlFilters 		= new ArrayList<QueryFilterItem>();
		List<Field> entityDeclaredFields 		= EntityUtil.getDeclaredFields(entityClass);

		log.info("=======FILTER: {}", filter);
		
		filter.put(TABLE_NAME, tableName);

		for (final String rawKey : filter.keySet()) {
			log.info("................." + rawKey + ":" + filter.get(rawKey));
			
			if (filter.get(rawKey) == null)
				continue;

			String currentKey = rawKey;
			boolean itemExacts = allItemExactSearch; 
			String filterTableName = tableName; 
			String finalNameAfterExactChecking = currentItemExact(rawKey);
			
			if(null != finalNameAfterExactChecking) {
				currentKey = finalNameAfterExactChecking;
				itemExacts = true;
			}
			
			log.info("Raw key: {} Now KEY: {}", rawKey, currentKey); 
			
			QueryFilterItem queryItem = new QueryFilterItem();
			queryItem.setExacts(itemExacts);

			// check if date
			QueryFilterItem dateFilterSql = getDateFilter(rawKey, currentKey, entityDeclaredFields, filter);

			if(null != dateFilterSql) {
				sqlFilters.add(dateFilterSql);
				continue;
			}
			
			Field field = getFieldByName(currentKey, entityDeclaredFields);

			if (field == null) {
				log.warn("Field Not Found :" + currentKey + " !");
				continue; 
			}
			
			String filterColumnName = getColumnName(field); 
			KeyValue joinColumnResult = checkIfJoinColumn(currentKey, field);
			
			if(null != joinColumnResult) {
				if(joinColumnResult.isValid()) {
					filterTableName = joinColumnResult.getKey().toString();
					filterColumnName = joinColumnResult.getValue().toString();
				} else {
					continue;
				}
			}
			
			queryItem.setTableName(filterTableName);
			queryItem.setColumnName(filterColumnName);
			queryItem.setValue(filter.get(rawKey));
			sqlFilters.add(queryItem ); 
		} 
 
		return completeWhereClause(sqlFilters);
		 
	}
	
	private static KeyValue checkIfJoinColumn(String currentKey, Field field) {
		String multiKeyColumnName = getMultiKeyColumnName(currentKey);
		KeyValue keyValue = new KeyValue();
		boolean isMultiKey 	= null != multiKeyColumnName; 
		boolean validColumn = false;
		
		if (field.getAnnotation(JoinColumn.class) != null || isMultiKey) { 

			try {
				Class fieldClass 		= field.getType();
				String joinTableName 	= getTableName(fieldClass); 
				String referenceFieldName = "";

				if (isMultiKey) {
					referenceFieldName = multiKeyColumnName;
				}else {
					referenceFieldName = getOptionItemName(field);
				}

				Field 	referenceEntityField 	= getDeclaredField(fieldClass, referenceFieldName);
				String 	fieldColumnName 		= getColumnName(referenceEntityField);

				if (fieldColumnName == null || fieldColumnName.equals("")) {
					validColumn = false;
				}else {
				  
					keyValue.setKey(joinTableName);
					keyValue.setValue(fieldColumnName);
					validColumn = true;
				}
				
			} catch ( Exception e) {
				
				log.warn(e.getClass() + " " + e.getMessage() + " " + field.getType());
				e.printStackTrace(); 
				validColumn = false;
			}

		} else { 
			return null;
		} 
		
		keyValue.setValid(validColumn);
		return keyValue;
	}

	private static String getMultiKeyColumnName(String currentKey) {
		String[] multiKey 	= currentKey.split(",");
		boolean isMultiKey 	= multiKey.length == 2;
		if (isMultiKey) {
			return  multiKey[1]; 
		} 
		else {
			return null;
		}
	}

	private static String completeWhereClause(List<QueryFilterItem> sqlFilters) {
		String whereClause = "";

		if (sqlFilters.size() > 0) {
			whereClause = generateQueryFilterString( sqlFilters);
		} else {
			return "";
		} 

		String result = SQL_KEYWORD_WHERE.concat(whereClause); 
		return result;
	}

	private static String currentItemExact(String rawKey) { 
		if (rawKey.endsWith("[EXACTS]")) { 
			String finalKey = rawKey.split("\\[EXACTS\\]")[0];
			log.info("{} exact search",finalKey);
			return finalKey;
		}
		return null;
	}

	private static String getOptionItemName(Field field) {
		FormField formField 	= field.getAnnotation(FormField.class);
		String referenceFieldName = formField.optionItemName();
		return referenceFieldName;
	}

	private static String generateQueryFilterString(List<QueryFilterItem> queryFilterItems) {
		List<String> listOfSqlFilter = new ArrayList<String>();
		for (int i = 0; i < queryFilterItems.size(); i++) {
			String sqlString = queryFilterItems.get(i).generateSqlString();
			
			listOfSqlFilter.add(sqlString); 
		}
		return String.join(SQL_KEYWORD_AND, listOfSqlFilter);
	}

	/**
	 * generate date filter sql
	 * @param rawKey
	 * @param key
	 * @param fields
	 * @param filter
	 * @return
	 */
	private static QueryFilterItem getDateFilter(String rawKey, String key, List<Field > fields, Map filter) {
		boolean dayFilter 	= rawKey.endsWith(DAY_SUFFIX);
		boolean monthFilter = rawKey.endsWith(MONTH_SUFFIX);
		boolean yearFilter 	= rawKey.endsWith(YEAR_SUFFIX);
		

		if (dayFilter || monthFilter || yearFilter) {

			String fieldName	= key;
			String mode 		= FILTER_DATE_DAY; 
			
			if (dayFilter) {
				fieldName 	= key.replace(DAY_SUFFIX, "");
				mode 		= FILTER_DATE_DAY;

			} else if (monthFilter) {
				fieldName 	= key.replace(MONTH_SUFFIX, "");
				mode 		= FILTER_DATE_MON1TH;

			} else if (yearFilter) {
				fieldName	= key.replace(YEAR_SUFFIX, "");
				mode 		= FILTER_DATE_YEAR;

			}

			Field field = getFieldByName(fieldName, fields);

			if (field == null) {
				log.warn("FIELD NOT FOUND: " + fieldName + " !");
				return null;

			}

			String columnName = getColumnName(field);
			String tableName = filter.get(TABLE_NAME).toString(); 
			
			QueryFilterItem queryItem = new QueryFilterItem();
			queryItem.setTableName(tableName);
			queryItem.setColumnName(columnName);
			queryItem.setDateMode(mode);
			queryItem.setValue( filter.get(key).toString());
			queryItem.setExacts(true);
			 
			
			return queryItem;
		}
		return null;
	} 
  
	private static String orderSQL(Class entityClass, String orderType, String orderBy) {

		/**
		 * order by field
		 */
		Field orderByField = getDeclaredField(entityClass, orderBy);

		if (orderByField == null) {
			return null;
		}
		Field idField = EntityUtil.getIdField(entityClass);

		if (idField == null) {
			return null;
		}
		String columnName 	= idField.getName();
		String tableName 	= getTableName(entityClass);

		if (orderByField.getAnnotation(JoinColumn.class) != null) {
			
			Class fieldClass 	= orderByField.getType();
			FormField formField = orderByField.getAnnotation(FormField.class);
			tableName 			= getTableName(fieldClass);
			

			try {
				Field fieldField = fieldClass.getDeclaredField(formField.optionItemName());
				columnName = getColumnName(fieldField);

			} catch ( Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			columnName = getColumnName(orderByField);
		}

		String orderField = doubleQuoteMysql(tableName).concat(".").concat(doubleQuoteMysql(columnName));

		return buildString(SQL_KEYWORD_ORDERBY, orderField, orderType);
	}

	private static String getTableName(Class entityClass) {
		log.info("getTableName From entity class: " + entityClass.getCanonicalName());
		
		Table table = (Table) entityClass.getAnnotation(Table.class);

		if (table != null) {

			if (table.name() != null && !table.name().equals("")) {
				return table.name();
			}
		}
		return entityClass.getSimpleName().toLowerCase();
	}
	
	 
	/**
	 * generate sql Select and sql Select Count (*)
	 * @param filter
	 * @param entityClass
	 * @return
	 */
	public static QueryHolder generateSqlByFilter(Filter filter, Class<? extends BaseEntity> entityClass ) {

		log.info("CRITERIA-FILTER: {}", filter);
		log.info("entity class: {}", entityClass);

		int		offset 				= filter.getPage() * filter.getLimit();
		boolean withLimit 			= filter.getLimit() > 0;
		boolean withOrder 			= filter.getOrderBy() != null && filter.getOrderType() != null
				&& !filter.getOrderBy().equals("") && !filter.getOrderType().equals("");
		boolean contains 			= filter.isContains();
		boolean exacts 				= filter.isExacts();
		boolean withFilteredField 	= filter.getFieldsFilter() != null;

		String orderType 		= filter.getOrderType();
		String orderBy 			= filter.getOrderBy();
		String orderSQL 		= withOrder ? orderSQL(entityClass, orderType, orderBy) : "";

		String limitOffsetSQL 	= "";
		String filterSQL		= "";

		String tableName 		= getTableName(entityClass);
		String joinSql			= createLeftJoinSQL(entityClass);
		
		if(withLimit) {
			limitOffsetSQL = buildString(
					SQL_KEYWORD_LIMIT,
					String.valueOf(filter.getLimit()), 
					SQL_KEYWORD_OFFSET, 
					String.valueOf(offset));
		}
		
		if(withFilteredField) {
			filterSQL = createWhereClause(
					entityClass, 
					filter.getFieldsFilter(), 
//					contains, 
					exacts 
					);
		}  

		String sql = buildString(
				SQL_KEYWORD_SELECT, 
				doubleQuoteMysql(tableName), 
				".*", 
				SQL_KEYWORD_FROM,
				doubleQuoteMysql(tableName),
				joinSql, 
				filterSQL, 
				orderSQL, 
				limitOffsetSQL);

		String sqlCount = buildString(
				SQL_KEYWORDSET_SELECT_COUNT, 
				doubleQuoteMysql(tableName), 
				joinSql, 
				filterSQL);

		log.info("query select: {}", sql);
		log.info("query count: {}", sqlCount);
		
		return new QueryHolder(sql, sqlCount);
	}

	static String doubleQuoteMysql(Object str) {
		return StringUtil.doubleQuoteMysql(str.toString());
	}

	
	
	

}
