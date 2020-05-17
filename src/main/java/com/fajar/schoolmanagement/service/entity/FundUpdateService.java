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
public class FundUpdateService extends BaseEntityUpdateService {

	@Autowired
	protected EntityRepository entityRepository;
	@Autowired
	private CashBalanceService cashBalanceService;

	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord, EntityUpdateInterceptor updateInterceptor) {
		BaseEntity fund = copyNewElement(entity, newRecord);

		CapitalFlow newEntity = entityRepository.save(fund);
		
		log.info("fund object : {}", fund.getClass().getSimpleName());
		
		cashBalanceService.updateCashBalance(newEntity);

		return WebResponse.builder().entity(newEntity).build();
	}
}
