package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.util.EntityUtil.getDeclaredField;
import static com.fajar.schoolmanagement.util.MapUtil.objectEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fajar.schoolmanagement.dto.FieldType;
import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.setting.EntityElement;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.util.DateUtil;

public class ReportMappingUtil {
	private static final String DATE_PATTERN = "dd-MM-yyyy' 'hh:mm:ss";

	/**
	 * get list size in the map<integer, list>
	 * @param mappedCashflow
	 * @return
	 */
	public static int getCashflowItemCount( Map<Integer, List<BaseEntity>> mappedCashflow) {
		int count = 0;
		for(Integer day:mappedCashflow.keySet()) {
			count += mappedCashflow.get(day).size();
		}
		return count;
	}
	
	public static int getMonthDays(Filter filter) {
		int year = filter.getYear();
		int monthIndex = filter.getMonth() - 1;
		Integer monthDays = DateUtil.getMonthsDay(year)[monthIndex];
		return monthDays;
	}
	
	public static Map<Integer, List<BaseEntity>> sortFinancialEntityByDayOfMonth(List<BaseEntity> funds,
			int monthDays) {
		Map<Integer, List<BaseEntity>> mappedFunds = fillMapKeysWithNumber(monthDays);

		for (int i = 0; i < funds.size(); i++) {
			BaseEntity fund = funds.get(i);

			int transactionDay = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.DAY_OF_MONTH);
			mappedFunds.get(transactionDay).add(fund);
		}

		return mappedFunds;
	}

	public static Map<Integer, List<BaseEntity>> sortFinancialEntityByMonth(List<BaseEntity> funds) {
		Map<Integer, List<BaseEntity>> mappedFunds = fillMapKeysWithNumber(12);

		for (int i = 0; i < funds.size(); i++) {
			BaseEntity fund = funds.get(i);

			int transactionDay = DateUtil.getCalendarItem(fund.getTransactionDate(), Calendar.MONTH) + 1;
			mappedFunds.get(transactionDay).add(fund);
		}

		return mappedFunds;
	}

	public static Map<Integer, List<BaseEntity>> fillMapKeysWithNumber(int dayCount) {
		Map<Integer, List<BaseEntity>> map = new HashMap<Integer, List<BaseEntity>>();
		for (int day = 1; day <= dayCount; day++) {
			map.put(day, new ArrayList<>());
		}
		return map;
	}
	
	public static Object[] getEntitiesTableValues(List<BaseEntity> entities, EntityProperty entityProperty) {

		List<EntityElement> entityElements = entityProperty.getElements();
		Object[] values = new Object[(entities.size()+1) * (entityElements.size() + 1)];
		int seqNum = 0;
		
		/**
		 * column header
		 */
		values[seqNum] = "No";
		seqNum++;
		for (int i = 0; i < entityElements.size(); i++) {
			values[seqNum] = entityElements.get(i).getLableName();
			seqNum++;
		}
		
		/**
		 * table content
		 */
		for (int e = 0; e< entities.size(); e++) {  
			
			BaseEntity entity = entities.get(e); 
			values[seqNum] =  e+1 ; //numbering
			seqNum++;
			
			/**
			 * checking the value type
			 */
			elementLoop: for(int i = 0; i< entityElements.size();i++) {
				
				Object value = mapEntityValue(entity, entityElements.get(i));
				values[seqNum] = value;
				seqNum++;
				 
			} 
		}
		
		return values;
	}
	
	private static Object mapEntityValue(BaseEntity entity, EntityElement element ) { 
		final Field field = getDeclaredField(entity.getClass(), element.getId());
		final String fieldType = element.getType();
		Object value;
		
		try {
			value = field.get(entity);
			
			if(null != value) {
				
				if( objectEquals(fieldType, FieldType.FIELD_TYPE_DYNAMIC_LIST.value, FieldType.FIELD_TYPE_FIXED_LIST.value)){
					
					String optionItemName = element.getOptionItemName();
					
					if(null != optionItemName && StringUtils.isEmpty(optionItemName) == false) {
						
						Field converterField = getDeclaredField(field.getType(), optionItemName);
						Object converterValue = converterField.get(value);
						value = converterValue;
						
					}else {
						value = value.toString(); 
					}
					
				}else if(objectEquals(fieldType, FieldType.FIELD_TYPE_IMAGE.value)) {
				
					value = value.toString().split("~")[0];
//					values[seqNum] = ComponentBuilder.imageLabel(UrlConstants.URL_IMAGE+value, 100, 100);
//					continue elementLoop;
					
				}else if(objectEquals(fieldType, FieldType.FIELD_TYPE_DATE.value)) {
					
					value = DateUtil.formatDate((Date)value, DATE_PATTERN);
					
				}else if(objectEquals(fieldType, FieldType.FIELD_TYPE_NUMBER.value)) {
					
					value = Double.parseDouble(value.toString()); 
				}   
			}  
			
			return  value ;
		} catch ( Exception ex) { 
			ex.printStackTrace();
			return null;
		} 
	}
	
	public static String getReportDateString() { 
		return DateUtil.formatDate(new Date(), "ddMMyyyy'T'hhmmss-a");
	}

}
