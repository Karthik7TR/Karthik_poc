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
	
</head>

<body>
 	<h2>Filters</h2>
	<form:form action="<%=WebConstants.MVC_BOOK_LIBRARY_LIST%>"
			   commandName="<%=BookLibraryFilterForm.FORM_NAME%>" name="bookLibraryFilterForm" method="post">
			   <form:label path="name"/>
			   <form:input path="name"/><br>
			   <form:label path="from"/>
			   <form:input path="from"/>
			   <form:label path="to"/>
			   <form:input path="to"/><br>
			   <form:label path="userName"/>
			   <form:input path="userName"/><br>
			   <form:label path="eBookDefStatus"/>
			   <form:input path="eBookDefStatus"/><br>
			   <form:label path="publishingSttaus"/>
			   <form:input path="publishingSttaus"/><br>
			   <form:label path="titleId"/>
			   <form:input path="titleId"/><br>
			   <form:label path="isbn"/>
			   <form:input path="isbn"/><br>
			   <form:label path="authorName"/>
			   <form:input path="authorName"/><br>
			   <form:label path="materialNumber"/>
			   <form:input path="materialNumber"/><br>
			   <form:label path="publisher"/>
			   <form:input path="publisher"/><br>
		
	</form:form>
</body>
</html>
