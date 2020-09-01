package com.fajar.schoolmanagement.service.report;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.entity.setting.EntityPropertyBuilder;
import com.fajar.schoolmanagement.report.builder.EntityReportBuilder;
import com.fajar.schoolmanagement.service.ProgressService;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.EntityUtil;
import com.fajar.schoolmanagement.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityReportService {

	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ProgressService progressService;

	public XSSFWorkbook getEntityReport(List<BaseEntity> entities, Class<? extends BaseEntity> entityClass,
			HttpServletRequest httpRequest) throws Exception {
		log.info("Generate entity report: {}", entityClass); 
		User currentUser = SessionUtil.getUserFromRequest(httpRequest); 
		String requestId = currentUser.getRequestId();
		
		EntityProperty entityProperty = new EntityPropertyBuilder(entityClass ).createEntityProperty();
		ReportData reportData = ReportData.builder().entities(entities).entityProperty(entityProperty).requestId(requestId).build(); 
	
		EntityReportBuilder reportBuilder = new EntityReportBuilder(webConfigService, reportData);
		reportBuilder.setProgressService(progressService);
		
		progressService.sendProgress(1, 1, 10, false, requestId);

		XSSFWorkbook file = reportBuilder.buildReport(); 
		
		
		log.info("Entity Report generated");

		return file;
	}

}
