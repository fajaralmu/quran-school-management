package com.fajar.schoolmanagement.repository;

import java.util.List;

import javax.persistence.Query;

import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.querybuilder.QueryHolder;

public interface RepositoryCustom {

	public <T> List<T> filterAndSort(String q, Class<? extends T> objectClass);

	public <T> List<T> filterAndSort(QueryHolder queryHolder, Class<? extends T> objectClass);

	public Object getSingleResult(QueryHolder queryHolder);

	public Object getSingleResult(String q);

	public Query createNativeQuery(String sql);

	public <T> T getCustomedObjectFromNativeQuery(String sql, Class<T> objectClass);
	
	public <T extends BaseEntity> List<T> filterAndSortv2(Class<T> _class, Filter filter);
	
	public long getRowCount(Class<? extends BaseEntity> _class, Filter filter);
	
	public <T extends BaseEntity> T saveObject(T entity);
	
	public <T> T pesistOperation(PersistenceOperation<T> persistenceOperation);

}
