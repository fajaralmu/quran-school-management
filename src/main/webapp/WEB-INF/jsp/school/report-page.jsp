<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
.report-item {
	width: 100%;
	height: 250px;
	border: solid 2px maroon;
	padding: 10px;
}
</style>
<div class="content" style="width: 100%">

	<div id="content-report">
		<h2>Report Page</h2>
		<p>Good ${timeGreeting}, ${loggedUser.displayName}. Please select
			report you want to generate!</p>


		<div
			style="display: grid; grid-template-columns: 30% 30% 30%; grid-column-gap: 10px; grid-row-gap: 10px">
			<div class="report-item">
				<h3>Filter</h3>
				<h4>Month</h4>
				<select id="select-month">
					<c:forEach var="month" items="${months }">
						<option value="${month.value }">${month.key }</option>
					</c:forEach>
				</select>
				<h4>Year</h4>
				<input id="input-year" type="number" />
			</div>
			<c:forEach var="reportMenu" items="${reportMenus }">
				<div class="report-item">
					<h3>${reportMenu.name }</h3>
					<p>${reportMenu.description }</p>
					<button class="btn btn-info report-process" name="${reportMenu.url }">process</button>
				</div>
			</c:forEach>
		</div>
	</div>
</div>
<script type="text/javascript">
	var selectedMonth = 1;
	var selectedYear = new Date().getFullYear(); 
	
	function getRequestObject(month, year) {
		var reqObj = {
			filter : {}
		};
		if (month != null) {
			reqObj.filter.month = month;
		}
		if (year != null) {
			reqObj.filter.year = year;
		}
		return reqObj;
	}

	function printReport(endpoint) {

		var confirmed = confirm("Continue Generating Report?");
		if (confirmed != 1) {
			return;
		}
		var requestObject = getRequestObject(selectedMonth, selectedYear);
		console.log("requestObject: ", requestObject);
		postReq("<spring:url value="/api/report/" />" + endpoint,
				requestObject, function(xhr) {

					downloadFileFromResponse(xhr);

					infoDone();
				}, true);

	}

	function init() {
		 
		initButtonEvents();
		initFilterEvents();

	}
	
	function initFilterEvents(){
		document.getElementById("input-year").value = new Date().getFullYear();
		document.getElementById("input-year").onchange = function(e) {
			selectedYear = e.target.value;
		}
		document.getElementById("select-month").onchange = function(e) {
			selectedMonth = e.target.value;
		}
	}
	
	function initButtonEvents(){
		var buttons = document.getElementsByClassName("report-process");
		for(var i =0;i<buttons.length;i++){
			const button = buttons[i];
			button.onclick = function(e){
				printReport(e.target.name);
			}
		}
		
	}

	init();
</script>