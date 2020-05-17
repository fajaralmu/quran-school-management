
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header" style="height:auto"> 

	<div>
	<!-- <ul class="nav nav-tabs"> -->
		<ul class="nav  flex-column">
		
			<!-- Account Menu -->
			<c:if test="${loggedUser == null  }">
				<li class="nav-item "><a
					class="nav-link  ${page == 'login' ? 'active':'' }"
					href="<spring:url value="/account/login"/>">Log In </a></li>
			</c:if>
			<c:if test="${loggedUser != null }">
				<div class="dropdown">
					<button class="btn btn-primary dropdown-toggle" type="button"
						data-toggle="dropdown">
						${loggedUser.displayName }<span class="caret"></span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="<spring:url value="/management/profile"/>">Profile</a>  <a
							class="dropdown-item" href="#" onclick="logout()">Logout</a>
					</div>
				</div>
			</c:if>
			 
			<%--  
			<c:if test="${loggedUser != null }">
				<li class="nav-item"><a
					class="nav-link ${page == 'dashboard' ? 'active':'' }"
					href="<spring:url value="/admin/home"/>">Dashboard</a></li> 
			</c:if> --%>
		 
			<c:forEach var="pageItem" items="${pages}"> 
					<li class="nav-item"><a class="nav-link pagelink" id="${pageItem.code }" menupage = "${pageItem.isMenuPage() }"
						href="<spring:url value="${pageItem.link }"/>">${pageItem.name }</a></li>
				 
			</c:forEach>

		</ul>
	</div>
</div>
<script type="text/javascript">
	document.body.style.backgroundColor = "${shopProfile.color}";

	var pagesLink = document.getElementsByClassName("pagelink");
	var pageMenus = {};
	var ctxPath = "${contextPath}";
	function logout() {
		postReq(
				"<spring:url value="/api/account/logout" />",
				{},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {

						window.location.href = "<spring:url value="/account/login" />";
					} else {
						alert("LOGOUT FAILS");
					}
				});
	}
	
	function getCurrentPageCode(){
		postReq(
				"<spring:url value="/api/public/pagecode" />",
				{},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					var pageCode = response.code;
					_byId(pageCode).setAttribute("class", "nav-link active");
				}
		);
	}
	
	function initPagesLinkEvent(){
		for(let i = 0; i< pagesLink.length;i++){
			pageLink = pagesLink[i];
			pageLink.onmouseover = function(e){
				fetchMenus(e);
			};
		}
	}
	
	function fetchMenus(e){
		const pageCode = e.target.id;
		
		if(pageMenus[pageCode] == null){
			const url = "<spring:url value="/api/public/menus/" />" + pageCode;
		  	postReq(
					url, {},
					function(xhr) {
						infoDone();
						var response = (xhr.data);
						var menus = response.entities; 
						pageMenus[pageCode] = menus;
						showMenuList(pageCode);
					}
			);  
		}else{
			showMenuList(pageCode);
		}
		
		
	}
	
	function showMenuList(pageCode){
		const menus = pageMenus[pageCode];
		console.log("MENUS:",menus);
		const menuContainer = createGridWrapper(1, "100px");
		
		for (var i = 0; i < menus.length; i++) {
			const menu = menus[i];
			const link = createAnchor(menu.code, menu.name, menu.url);
			menuContainer.appendChild(link);
		}
		
		_byId(pageCode).parentElement.appendChild(menuContainer);
	}
	
	initPagesLinkEvent();
	getCurrentPageCode();
</script>