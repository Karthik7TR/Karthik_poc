<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants.SortProperty"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>


<html>
<head>
	<link rel="stylesheet" href="theme/layout.css">
	<link rel="stylesheet" href="theme/jquery.ui.datepicker.css">
  	
	<script>
		$(function() {
			$( "#datepicker" ).datepicker();
		});
	</script>
</head>

<body>
  <div class="majorDiv">
	<h2>Filters</h2>
	<form:form>
		<label>Name</label><br>
		<input type="text" value=""/><br>
		<label>Date Range</label><br>
		&nbsp; Start Date <input id="datepicker" path="startDate"/>
		&nbsp; End Date <input id="datepicker" path="endDate"/><br>
		<label>Content Type</label><br>
		<input type="text" value=""/><br>
		<label>User Name</label><br>
		<input type="text" value=""/><br>
		<label>eBook Def Status</label><br>
		<input type="text" value=""/><br>
		<label>Publishing Status</label><br>
		<input type="text" value=""/><br>
		<label>Title ID</label><br>
		<input type="text" value=""/><br>
		<label>ISBN</label><br>
		<input type="text" value=""/><br>
		<label>Author Name</label><br>
		<input type="text" value=""/><br>
		<label>Material Number</label><br>
		<input type="text" value=""/><br>
		<label>Publisher</label><br>
		<input type="text" value=""/><br>
		
		
		
		<input type="submit" value="Go"/>
		<input type="submit" value="Clear"/>
	</form:form>
  </div>
</body>
</html>
