package com.fajar.schoolmanagement.service.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.service.FileService;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BaseEntityUpdateService {

	@Autowired
	protected FileService fileService;

	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord, EntityUpdate updateInterceptor) {
		log.error("saveEntity Method not implemented");
		return WebResponse.failed("method not implemented");
	}

	protected BaseEntity copyNewElement(BaseEntity source, boolean newRecord) {
		return EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);
	}

	protected List removeNullItemFromArray(String[] array) {
		List<Object> result = new ArrayList<>();
		for (String string : array) {
			if (string != null) {
				result.add(string);
			}
		}
		return result;

	}
}
