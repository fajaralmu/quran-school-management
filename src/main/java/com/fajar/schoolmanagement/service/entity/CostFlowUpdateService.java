package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.service.transaction.CashBalanceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CostFlowUpdateService extends BaseEntityUpdateService<CostFlow> {

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService;

	private CostFlow theCostFlow;

	@Override
	public WebResponse saveEntity(CostFlow entity, boolean newRecord) {
		CostFlow costFlow = copyNewElement(entity, newRecord);

		theCostFlow = saveObject(costFlow);
		validateCostInfo();

		log.info("Cost Type: {}", theCostFlow.getCostType());
		cashBalanceService.updateCashBalance(theCostFlow);

		return WebResponse.builder().entity(theCostFlow).build();
	}

	private void validateCostInfo() {
		theCostFlow = entityRepository.findById(CostFlow.class, theCostFlow.getId());
		log.info("validated theCostFlow: {}", theCostFlow);
		if (null == theCostFlow) {
			throw new RuntimeException("INVALID COST FLOW");
		}
	}
}
