package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.service.CashBalanceService;

@Service
public class CapitalFlowUpdateService extends BaseEntityUpdateService{ 

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService;
	
	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord) {
		CapitalFlow capital = (CapitalFlow) copyNewElement(entity, newRecord);
		
//		if(newRecord) {
//			return ShopApiResponse.failed("Unable to update!");
//		}
		 
		BaseEntity newEntity = entityRepository.save(capital); 
		cashBalanceService.updateCashBalance(newEntity);
		
		return WebResponse.builder().entity(newEntity).build();
	}
}
