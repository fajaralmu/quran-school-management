package com.fajar.schoolmanagement.report.builder;

import java.io.File;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.setting.EntityProperty;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.MyFileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityReportBuilder extends ReportBuilder {
	private List<BaseEntity> entities;
	private EntityProperty entityProperty;

	public EntityReportBuilder(WebConfigService webConfigService, ReportData reportData) {
		super(webConfigService, reportData);
	}

	@Override
	protected void init() {
		entities = reportData.getEntities();
		entityProperty = reportData.getEntityProperty();
		
	}
	
	@Override
	public File buildReport() { 

		log.info("Writing entity report of: {}", entityProperty.getEntityName());

		String time = getDateTime();
		String sheetName = entityProperty.getEntityName();

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time + ".xlsx";
		XSSFWorkbook xwb = new XSSFWorkbook();
		xsheet = xwb.createSheet(sheetName);

		createEntityTable();

		File file = MyFileUtil.getFile(xwb, reportName);
		return file;
	}

	private void createEntityTable() {
		try {
			Object[] entityValues = ExcelReportUtil.getEntitiesTableValues(entities, entityProperty);
			ExcelReportUtil.createTable(xsheet, entityProperty.getElements().size() + 1, 2, 2, entityValues);

		} catch (Exception e) {
			log.error("Error creating entity excel table");
			e.printStackTrace();
		}
	}

	

}
