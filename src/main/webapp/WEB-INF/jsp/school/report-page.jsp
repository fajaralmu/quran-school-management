<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
	.report-item{
		width: 100%;
		height: 200px;
		border: solid 2px maroon; 
	}
</style>
<div class="content" style="width: 100%">

	<div id="content-report">
		<h2>Report Page</h2>
		<p>Good ${timeGreeting}, ${loggedUser.displayName}. Please select
			report you want to generate!</p>
		<div style="display: grid; grid-template-columns: 30% 30% 30%; grid-column-gap: 10px">
			<div class="report-item">
				<h3>Month</h3>
				<select id="select-month">
					<c:forEach var="month" items="${months }">
						<option value="${month.value }">${month.key }</option>
					</c:forEach>
				</select>
				<h3>Year</h3>
				<input id="input-year" type="number" />
			</div>
			<div class="report-item">
				<button id="btn-report-monthly"  >Laporan Keuangan Bulanan</button>
			</div>
			<div class="report-item">
				<button id="btn-report-donation-student" >Laporan Infaq Bulanan Siswa</button> 
			</div>
			<div class="report-item">
				<button id="btn-report-donation-thursday-cashflow"  >Lapotan Mutasi Infaq Kamis</button> 
			</div>
			<div class="report-item">
				<button id="btn-report-donation-thursday-fundhflow"  >Lapotan Pemasukan Infaq Kamis</button> 
			</div>
		</div>
	</div>
</div>
<script type="text/javascript"> 
	var selectedMonth = 1;
	var selectedYear = new Date().getFullYear();

	function generateReportMonthly(e){
		printReport("monthlygeneralcashflow");
	}
	function generateReportDonationStudent(e){
		printReport("studentdonationreport");
	}
	function generateReportDonationThuCashflow(e){
		printReport("thrusdaydonationcashflow");
	}
	function generateReportDonationThuFundflow(e){
		printReport("thrusdaydonationfundflow");
	}
	function getRequestObject(month, year){
		var reqObj = {
				filter:{
					
				}
		};
		if(month!=null){
			reqObj.filter.month = month;
		}
		if(year!=null){
			reqObj.filter.year = year;
		}
		return reqObj;
	}

	function printReport( endpoint) {
		
		var confirmed = confirm("Continue Generating Report?");
		if(confirmed != 1){
			return;
		}
		var requestObject = getRequestObject(selectedMonth, selectedYear);
		console.log("requestObject: ",requestObject);
		postReq("<spring:url value="/api/report/" />" + endpoint,
				requestObject, function(xhr) {

					downloadFileFromResponse(xhr);

					infoDone();
				}, true);

	}

	function init() {
		document.getElementById("btn-report-monthly").onclick = function(e){
			generateReportMonthly(e);
		}
		document.getElementById("btn-report-donation-student").onclick = function(e){
			generateReportDonationStudent(e);
		}
		document.getElementById("btn-report-donation-thursday-cashflow").onclick = function(e){
			generateReportDonationThuCashflow(e);
		}
		document.getElementById("btn-report-donation-thursday-fundhflow").onclick = function(e){
			generateReportDonationThuFundflow(e);
		}
		document.getElementById("input-year").value = new Date().getFullYear();
		document.getElementById("input-year").onchange = function(e){
			selectedYear = e.target.value;
		}
		document.getElementById("select-month").onchange = function(e){
			selectedMonth = e.target.value;
		}
		
	}
	
	init();
</script>