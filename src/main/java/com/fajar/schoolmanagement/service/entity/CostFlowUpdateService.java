package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.service.transaction.CashBalanceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CostFlowUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	
	private CostFlow theCostFlow;
	
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord, EntityUpdateInterceptor updateInterceptor) {
		CostFlow costFlow = (CostFlow) copyNewElement(entity, newRecord); 
		 
		theCostFlow = entityRepository.save(costFlow);
		validateCostInfo();
		log.info("Cost Type: {}", theCostFlow.getCostType());
		cashBalanceService.updateCashBalance(theCostFlow);
		
		return WebResponse.builder().entity(theCostFlow).build();
	}

	private void validateCostInfo() { 
		theCostFlow = (CostFlow) entityRepository.findById(theCostFlow.getClass(), theCostFlow.getId());
	}
}
