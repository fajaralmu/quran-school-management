package com.fajar.schoolmanagement.service.report;

import static com.fajar.schoolmanagement.service.report.ExcelReportUtil.createTable;
import static com.fajar.schoolmanagement.service.report.ReportMappingUtil.getEntitiesTableValues;
import static com.fajar.schoolmanagement.service.report.ReportMappingUtil.getReportDateString;
import static com.fajar.schoolmanagement.util.FileUtil.getFile;

import java.io.File;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntityReportService {

	@Autowired
	private WebConfigService webConfigService;

	public File getEntityReport(List<BaseEntity> entities, EntityProperty entityProperty) {
		String time = getReportDateString();
		String sheetName = entityProperty.getEntityName();
		log.info("generate entity report: {}", entityProperty.getEntityName());

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFSheet xsheet = xwb.createSheet(sheetName);

		Object[] entityValues = getEntitiesTableValues(entities, entityProperty);
		createTable(xsheet, entityProperty.getElements().size() + 1, 2, 2, entityValues);

		File file = getFile(xwb, reportName);
		return file;
	}

	public File generateEntityReport(List<BaseEntity> entities, Class<? extends BaseEntity> entityClass) {
		log.info("entity: {}, entities count: {}",entityClass, entities.size());
		EntityProperty entityProperty = EntityUtil.createEntityProperty(entityClass, null);
		
		File result = getEntityReport(entities, entityProperty);
		return result;
	}

	
}
