package com.fajar.schoolmanagement.service.transaction;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.dto.Filter;
import com.fajar.schoolmanagement.dto.ReportData;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.DonationThursday;
import com.fajar.schoolmanagement.repository.CapitalFlowRepository;
import com.fajar.schoolmanagement.repository.CostFlowRepository;
import com.fajar.schoolmanagement.repository.DonationMonthlyRepository;
import com.fajar.schoolmanagement.repository.DonationThursdayRepository;
import com.fajar.schoolmanagement.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {
	
	@Autowired
	private CashBalanceService cashBalanceService;
	@Autowired
	private DonationMonthlyRepository donationMonthlyRepository;
	@Autowired
	private DonationThursdayRepository donationThursdayRepository;
	@Autowired
	private CapitalFlowRepository capitalFlowRepository;
	@Autowired
	private CostFlowRepository costFlowRepository;

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public ReportData getMonthlyGeneralCashflow(Filter filter) {
		int month = filter.getMonth();
		int year = filter.getYear();
		
		CashBalance lastBalance = cashBalanceService.getBalanceBefore(month, year, false);
		//get flow of funds
		List<BaseEntity> fundFlows = getFundFlows(month, year);
		List<BaseEntity> spendings = getFundSpent(month, year);
		
		ReportData reportData = new ReportData();
		reportData.setInitialBalance(lastBalance);
		reportData.setFunds(fundFlows);
		reportData.setSpendings(spendings);
		reportData.setFilter(filter);
		
		return reportData;
	}
	
	public ReportData getYearlyThrusdayDonationCashflow(Filter filter) {
		int year = filter.getYear();
		
		CashBalance lastBalance = cashBalanceService.getBalanceBefore(1, year, true); 
		List<DonationThursday> funds = donationThursdayRepository.findByYear(year);
		List<CostFlow> spendings = costFlowRepository.findBySourceOfFundAndYear(SourceOfFund.DONATION_THRUSDAY.toString(), year);
		
		ReportData reportData = new ReportData();
		reportData.setInitialBalance(lastBalance);
		reportData.setFunds(CollectionUtil.convertList(funds));
		reportData.setSpendings(CollectionUtil.convertList(spendings));
		reportData.setFilter(filter);
		
		return reportData ;
	}

	public ReportData getYearlyMonthlyDonationCashflow(Filter filter) {
		int year = filter.getYear();
		
		List<DonationMonthly> funds = donationMonthlyRepository.findByYear(year);
		
		ReportData reportData = new ReportData();
		reportData.setFunds(CollectionUtil.convertList(funds));
		reportData.setFilter(filter);
		
		return reportData ;
	}
	
	private List<BaseEntity> getFundSpent(int month, int year) {
		List<BaseEntity> spendings = new ArrayList<BaseEntity>(); 
		
		List<CostFlow> cost = costFlowRepository.findByPeriod(month, year); 
		spendings.addAll(cost);
		return spendings;
	}

	private List<BaseEntity> getFundFlows(int month, int year) {

		List<BaseEntity> funds = new ArrayList<BaseEntity>();

		List<DonationMonthly> monthlyDonation = donationMonthlyRepository.findByMonthAndYear(month, year); 
		List<DonationThursday> thrusdayDonation = donationThursdayRepository.findByMonthAndYear(month, year);
		List<CapitalFlow> capitalFlows = capitalFlowRepository.findByPeriod(month, year);
		
		funds.addAll(monthlyDonation);
		funds.addAll(thrusdayDonation);
		funds.addAll(capitalFlows);
		
		return funds;
	}
}
