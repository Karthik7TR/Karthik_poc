<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.book.CreateBookForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
<head>
	<link rel="stylesheet" href="theme/dashboard.css">
</head>

<body>
  <jsp:include page="stdHeader.jsp"/>
  <div class="majorDiv">
	<h2>Create Book</h2>
	<form:form action="<%=WebConstants.URL_CREATE_BOOK%>"
			   commandName="<%=CreateBookForm.FORM_NAME%>" name="theForm" method="post">
			   
		<%-- Validation error Message Presentation --%>
		<spring:hasBindErrors name="<%=CreateBookForm.FORM_NAME%>">
			<div class="errorBox">
		      <b><spring:message code="please.fix.errors"/></b><br/>
		      <form:errors path="*">
		      	<ul>
				<c:forEach items="${messages}" var="message">
					<li style="color: black">${message}</li>
				</c:forEach>
		      	</ul>
			  </form:errors>
			  <br/>
		    </div>
		    <br/>
	    </spring:hasBindErrors>
	    
	    <%-- Informational Messages area --%>
	    <c:if test="${infoMessage != null}">
	    <div style="background: lightgreen; padding: 5px 5px 5px 5px;">
	    	${infoMessage}
	    </div>
	    <br/>
	    </c:if>
	    <%-- Error Messages area --%>
	    <c:if test="${errMessage != null}">
	    <div style="background: orange; padding: 5px 5px 5px 5px;">
	    	${errMessage}
	    </div>
	    <br/>
	    </c:if>
	    
		<table>
		  <tr>
			<td>Book</td>	<%-- Unique book discriminate --%>
			<td>
			  <form:select path="bookKeyString">  <%-- form: titleId,majorVersion --%>
			    <form:options items="${bookOptions}" itemLabel="label" itemValue="value"/>
			  </form:select>
			</td>
		  <tr>
			<td>Job Priority&nbsp;</td>  <%-- Indicates which launch queue to place job request on --%>
			<td>
			  <form:select path="highPriorityJob">
			    <form:option label="Normal" value="false"/>
				<form:option label="High" value="true"/>
			  </form:select>
			 </td>
		  </tr>
		</table>
		<br/>
		<input type="submit" value="Create Book"/>
	</form:form>
  </div>
</body>
</html>
