<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
  xmlns:c="http://java.sun.com/jsp/jstl/core" 
  version="2.0">
	



<!--%@ taglib prefix="f"  uri="http://java.sun.com/jsf/core"%-->
<!--%@ taglib prefix="h"  uri="http://java.sun.com/jsf/html"%-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Gatherer- Novus</title>
</head>
<body>
	<h2>Get Toc Request Form</h2>
	<form:form method="post" action="getTocData.html">
		<c:if test="${not empty tocObjects}">
		     <table>
		              <c:forEach var="toc" items="${tocObjects}">
		                           <tr>
		                              <td>${toc.name}</td>
		                              <td>${toc.guid}</td>
		                              <td>${toc.rootGuid}</td>
		                              <td>${toc.parentGuid}</td>
		                              <td>${toc.metadata}</td>
		                              <td>${toc.docGuid}</td>
		                              <td>${toc.childrenCount}</td>
		                           </tr>
		              </c:forEach>
		      </table>
		 </c:if> 
	</form:form>
</body>
</html>
