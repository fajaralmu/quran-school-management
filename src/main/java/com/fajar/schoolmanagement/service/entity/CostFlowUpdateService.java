package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.service.CashBalanceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CostFlowUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord, EntityUpdateInterceptor updateInterceptor) {
		CostFlow costFlow = (CostFlow) copyNewElement(entity, newRecord); 
		 
		CostFlow newEntity = entityRepository.save(costFlow);
		log.info("Cost Type: {}", newEntity.getCostType());
		cashBalanceService.updateCashBalance(newEntity);
		
		return WebResponse.builder().entity(newEntity).build();
	}
}
