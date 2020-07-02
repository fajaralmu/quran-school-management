package com.fajar.schoolmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.service.transaction.CashType;

public interface CashBalanceRepository extends JpaRepository<CashBalance, Long> {

	public CashBalance findTop1ByOrderByIdDesc();

//	@Query(nativeQuery = true, value = "select * from cash_balance where month(date) = ?1 and year(date) = ?2 order by id  desc limit 1")
//	public CashBalance getCashBalanceAt(int month, int year);

	/**
	 * 
	 * @param dateString 'yyyy-MM-dd' pattern
	 * @return Object[]
	 */
	@Query(nativeQuery = true, value = "select "
			+ " sum(creditAmount) as credit, sum(debitAmount) as debit, (sum(debitAmount) - sum(creditAmount)) as balance from fin_balance where "
			+ " date < ?1")
	public Object getBalanceBefore(String dateString);

	@Query(nativeQuery = true, value = "select "
			+ " sum(creditAmount) as credit, sum(debitAmount) as debit, (sum(debitAmount) - sum(creditAmount)) as balance from fin_balance where "
			+ " date < ?1 and type = 'DONATION_THURSDAY'")
	public Object getDonationThrusdayBalanceBefore(String dateString);

	public CashBalance findTop1ByTypeAndReferenceId(CashType type, String id);

	@Query(nativeQuery = true, value = "select max(year(fin_balance.`date`)) from fin_balance")
	public int getMaxYear();

	@Query(nativeQuery = true, value = "select min(year(fin_balance.`date`)) from fin_balance")
	public int getMinYear();

}
