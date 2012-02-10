<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>


<html>
<head>
	<script type="text/javascript" src="js/jquery.js"></script>
  	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
  	<script>
		$(function() {
			$( "#datepickerFrom" ).datepicker();
			$( "#datepickerTo" ).datepicker();
		});
	</script>
</head>

<body>
 	<h2>Filters</h2>
	<form:form action="<%=WebConstants.MVC_BOOK_LIBRARY_LIST%>"
			   commandName="<%=BookLibraryFilterForm.FORM_NAME%>" name="bookLibraryFilterForm" method="post">
			   &nbsp;<form:label path="name">Name </form:label><br>
			   &nbsp;<form:input path="name"/><br>
			   &nbsp;<label>Date Range</label><br>
			   &nbsp;&nbsp;<form:label path="from">From</form:label>
			   &nbsp;<form:input id="datepickerFrom" path="from"/>
			   &nbsp;<form:label path="to">To</form:label>
			   &nbsp;<input id="datepickerTo" path="to"/><br>
			   &nbsp;<form:label path="userName">Username</form:label><br>
			   &nbsp;<form:input path="userName"/><br>
			   &nbsp;<form:label path="eBookDefStatus">eBook Status</form:label><br>
			   &nbsp;<form:input path="eBookDefStatus"/><br>
			   &nbsp;<form:label path="publishingSttaus">Publishing Status</form:label><br>
			   &nbsp;<form:input path="publishingSttaus"/><br>
			   &nbsp;<form:label path="titleId">Title Id</form:label><br>
			   &nbsp;<form:input path="titleId"/><br>
			   &nbsp;<form:label path="isbn">ISBN</form:label><br>
			   &nbsp;<form:input path="isbn"/><br>
			   &nbsp;<form:label path="authorName">Author Name</form:label><br>
			   &nbsp;<form:input path="authorName"/><br>
			   &nbsp;<form:label path="materialNumber">Material Number</form:label><br>
			   &nbsp;<form:input path="materialNumber"/><br>
			   &nbsp;<form:label path="publisher">Publisher</form:label><br>
			   &nbsp;<form:input path="publisher"/><br>
			   
			   <form:button>Go</form:button>
			   <form:button>Clear</form:button>
		
	</form:form>
</body>
</html>
