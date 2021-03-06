package com.fajar.schoolmanagement.service.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.FinancialEntity;
import com.fajar.schoolmanagement.service.transaction.CashBalanceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeneralFundUpdateService extends BaseEntityUpdateService<BaseEntity> {
	@Autowired
	private CashBalanceService cashBalanceService;

	private BaseEntity theFund;

	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord) {
		BaseEntity fund = copyNewElement(entity, newRecord);

		theFund = saveObject(fund);
		validateFundInfo();

		cashBalanceService.updateCashBalance((FinancialEntity) theFund);

		return WebResponse.builder().entity(theFund).build();
	}

	private void validateFundInfo() {
		try {
			theFund = entityRepository.findById(theFund.getClass(), theFund.getId());

		} catch (Exception e) {

			log.error("Error validating donation monthly");
			e.printStackTrace();
		}

	}
}
