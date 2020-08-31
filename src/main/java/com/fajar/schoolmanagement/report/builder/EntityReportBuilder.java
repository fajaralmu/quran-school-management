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
	private final String requestId;

	public EntityReportBuilder(WebConfigService webConfigService, ReportData reportData) {
		super(webConfigService, reportData);
		this.requestId = reportData.getRequestId();
	}
 
	
	@Override
	protected void init() {
		entities = reportData.getEntities();
		entityProperty = reportData.getEntityProperty();
		
	}
	
	@Override
	public XSSFWorkbook buildReport() { 

		log.info("Writing entity report of: {}", entityProperty.getEntityName());

		String time = getDateTime();
		String sheetName = entityProperty.getEntityName();

		String reportName = webConfigService.getReportPath() + "/" + sheetName + "_" + time +"_"+ requestId+".xlsx";
		xssfWorkbook = new XSSFWorkbook();
		xsheet = xssfWorkbook.createSheet(sheetName);

		createEntityTable();

		sendProgress(1, 1, 10 );
		
//		File file = MyFileUtil.getFile(xwb, reportName);
		sendProgress(1, 1, 10 );
		return xssfWorkbook;
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
