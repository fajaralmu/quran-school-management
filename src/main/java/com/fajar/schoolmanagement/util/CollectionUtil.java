package com.fajar.schoolmanagement.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.DonationMonthly;

public class CollectionUtil {
	public static <T> List<T> arrayToList(T[] array) {
		List<T> list = new ArrayList<T>();
		for (T t : array) {
			list.add(t);
		}
		return list;

	}

	public static void main(String[] args) {

	}

	public static List< BaseEntity> mapToList(Map<? extends Object, ? extends BaseEntity> map) {
		List< BaseEntity> list = new ArrayList<>();
		for (Object key : map.keySet()) {
			list.add(map.get(key));
		}

		return list;
	}
 
	public static <T> List listOf(Object o) {
		
		List list = new ArrayList<>();
		list.add(o);
		return list ;
	}

	public static <T> List  convertList(List list) {
		List<T> newList = new ArrayList<T>();
		for (Object object : list) {
			newList.add((T) object);
		}
		return newList;
	}

	public static String[] toArrayOfString(List validUrls) {
		if(validUrls == null) {
			return new String[] {};
		}
		String[] array = new String[validUrls.size()];
		for (int i = 0; i < validUrls.size(); i++) {
			array[i] = validUrls.get(i).toString();
		}
		return array;
	}

	public static void avoidNullArray(List<DonationMonthly> studentDonations) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	 
	
	

}
