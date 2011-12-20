<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>



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
		<table>
			<tr>
				<td><form:label path="contentType">ContentType</form:label></td>
				<td><form:input path="contentType" /></td>
			</tr>
			<tr>
				<td><form:label path="guid">Guid</form:label></td>
				<td><form:input path="guid" /></td>
			</tr>
			<tr>
				<td><form:label path="collection">Collection Name</form:label></td>
				<td><form:input path="collection" /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="Get Data" /></td>
			</tr>
		</table>
	</form:form>
</body>
</html>
