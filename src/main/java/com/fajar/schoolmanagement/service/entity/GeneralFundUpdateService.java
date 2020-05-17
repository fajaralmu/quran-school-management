package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.repository.EntityRepository;
import com.fajar.schoolmanagement.service.transaction.CashBalanceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeneralFundUpdateService extends BaseEntityUpdateService {

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService; 
	
	private BaseEntity theFund;

	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord, EntityUpdateInterceptor updateInterceptor) {
		BaseEntity fund = copyNewElement(entity, newRecord);

		theFund = entityRepository.save(fund);
		validateFundInfo();
		
		cashBalanceService.updateCashBalance(theFund);

		return WebResponse.builder().entity(theFund).build();
	}

	private void validateFundInfo() {
		try {
			theFund =   (BaseEntity) entityRepository.findById(theFund.getClass(), theFund.getId());
		
		}catch (Exception e) {
			
			log.error("Error validating donation monthly");
			e.printStackTrace();
		}
		
	}
}
