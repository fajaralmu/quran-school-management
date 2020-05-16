package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.financialjournal.CashBalanceService;
import com.fajar.schoolmanagement.repository.EntityRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CapitalFlowUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord, EntityUpdateInterceptor updateInterceptor) {
		CapitalFlow capital = (CapitalFlow) copyNewElement(entity, newRecord);
		 
		CapitalFlow newEntity = entityRepository.save(capital); 
		log.info("Fund Type: {}", newEntity.getFundType());
		cashBalanceService.updateCashBalance(newEntity);
		
		return WebResponse.builder().entity(newEntity).build();
	}
}
