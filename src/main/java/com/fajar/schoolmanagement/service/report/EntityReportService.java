package com.fajar.schoolmanagement.service.report;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.report.builder.EntityReportBuilder;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityReportService {

	@Autowired
	private WebConfigService webConfigService;

	public File getEntityReport(List<BaseEntity> entities, Class<? extends BaseEntity> entityClass) throws Exception {
		log.info("Generate entity report: {}", entityClass);
		
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityClass, null);
		ReportData reportData = ReportData.builder().entities(entities).entityProperty(entityProperty ).build();
		EntityReportBuilder reportBuilder = new EntityReportBuilder(webConfigService, reportData);
		 
		File file = reportBuilder.buildReport();
		log.info("Entity Report generated");
		
		return file;
	} 
	
}
