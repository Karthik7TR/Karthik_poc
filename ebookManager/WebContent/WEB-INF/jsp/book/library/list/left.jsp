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


  	<script>
		$(function() {
			$( "#datepickerFrom" ).datepicker();
			$( "#datepickerTo" ).datepicker();
		});
	</script>

 	<h2>Filters</h2>

	&nbsp;<label>Display Name </label><br>
	&nbsp;<input type="text" /><br>
	&nbsp;<label>Title ID</label><br>
	&nbsp;<input type="text" /><br>
	&nbsp;<label>ISBN</label><br>
	&nbsp;<input type="text"  /><br>
	&nbsp;<label>Material ID</label><br>
	&nbsp;<input type="text"  /><br>
	&nbsp;<label>Book Status</label><br>
	&nbsp;<input type="text" /><br>
	&nbsp;<label>Last Edit Date</label><br>
	&nbsp;&nbsp;<label>From</label>
	&nbsp;<input type="text" id="datepickerFrom" />
	&nbsp;&nbsp;<label>To</label>
	&nbsp;<input type="text" id="datepickerTo" /><br>
	
	
	
	<button disabled="disabled">Search</button>
	<button disabled="disabled">Reset</button>
