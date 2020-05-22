<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
.content-item {
	width: 100%;
	height: 50px;
	border: solid 2px maroon;
	padding: 10px;
}

.content-item:hover {
	background-color: lime;
	cursor: pointer;
}

.selected {
	background-color: lime;
}
</style>
<div class="content" style="width: 100%">

	<div id="content-report">
		<h2>Report Page</h2>
		<p>Good ${timeGreeting}, ${loggedUser.displayName}. Please select
			report you want to generate!</p>


		<div style="display: grid; grid-template-columns: 70% 20%;">
			<div id="pages" style="width: 100%; padding: 10px">
				
			</div>
			<div>
				<button class="btn btn-info" onclick="up()">Up</button>
				<button class="btn btn-info" onclick="down()">Down</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var contentItems;
	var selectedId = 0;
	var pagesContainer = document.getElementById("pages");
	var pages = {};

	function initEvents() {
		contentItems = document.getElementsByClassName("content-item"); 
		for (var i = 0; i < contentItems.length; i++) {
			const contentItem = contentItems[i];
			contentItem.onclick = function(e) {

				contentItemOnClick(contentItem);
			}
		}
	}

	function populatePages() {
		pagesContainer.innerHTML = "";
		for (var i = 0; i < pages.length; i++) {
			const page = pages[i];
			if(page == null){
				console.log("PAGE IS NULL");
				continue;
			}
			const pageElement = createEntityElements(page);
			pagesContainer.appendChild(pageElement);
		}
		initEvents();
	}

	function createEntityElements(entity) {

		var className = "content-item";
		if(entity.id == selectedId){
			className = "content-item selected";
		}
		
		const div = createHtmlTag("div", {
			"id" : entity.id,
			"class" : className,
			"child" : createHtmlTag("h3", {
				"innerHTML" : entity.name
			})
		});
		return div;
	}	

	function contentItemOnClick(contentItem) {
		refresh();
		contentItem.setAttribute("class", "content-item selected");
		selectedId = contentItem.id;
	}

	function refresh() {
		for (var i = 0; i < contentItems.length; i++) {
			const contentItem = contentItems[i];
			contentItem.setAttribute("class", "content-item");
		}
	}

	function up() {  
	  
		for (var i = 0; i < pages.length; i++) {
			const page = pages[i]; 
			if(page.id == selectedId){
				 
				const newIndex = getNewIndexUp(i, pages.length); 
				swapArray(newIndex, i, pages);
				break;
			}
			 
		}
		 
		populatePages();
		
	}
	
	function swapArray(index, indexToSwap, array){
		const oldValue = array[index];
		
		//swap
		array[index] = array[indexToSwap]; 
		array[indexToSwap] = oldValue;
	}
	
	function getNewIndexUp(currentIndex, arrayLength){
		var newIndex = currentIndex - 1;
		if(newIndex < 0){
			newIndex = arrayLength - 1;
		}
		return newIndex;
	}
	
	function getNewIndexDown(currentIndex, arrayLength){
		var newIndex = currentIndex + 1;
		if(newIndex >= arrayLength ){
			newIndex = 0;
		}
		return newIndex;
	}

	function down() {
		for (var i = 0; i < pages.length; i++) {
			const page = pages[i]; 
			if(page.id == selectedId){
				 
				const newIndex = getNewIndexDown(i, pages.length);
				swapArray(newIndex, i, pages);
				break;
			}
			 
		}
		 
		populatePages();
	}

	function fetchPages() {
		var requestObject = {
			"entity" : "page",
			"filter" : {
				"limit" : 0,
				"page" : 0
			}
		};

		doLoadEntities("<spring:url value="/api/entity/get" />", requestObject,
				function(response) {
					pages = response.entities;
					populatePages();
					 
					infoDone();
				}, true);
	}
	
	function getPageById(id){ 
		for (var i = 0; i < pages.length; i++) {
			if(pages[i].id == id){
				return pages[i];
			}
		}
		return null;
	}
	
	fetchPages();
</script>