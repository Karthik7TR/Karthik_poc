<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<html>
<head>

<body>
  <div class="majorDiv">
	
	<form:form action="<%=WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW%>"
			   commandName="<%=GenerateBookForm.FORM_NAME%>" name="theForm" method="post">
			   
		<%-- Validation error Message Presentation --%>
		<spring:hasBindErrors name="<%=GenerateBookForm.FORM_NAME%>">
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
	    
	    <table>
		<tr>
			<td>
				<form:hidden path="fullyQualifiedTitleId"/>
			</td>
		</tr>
		<tr>
			<td>Priority:&nbsp;</td>  <%-- Indicates which launch queue to place job request on --%>
			<td>
			  <form:select path="highPriorityJob">
			    <form:option label="Normal" value="false"/>
				<form:option label="High" value="true"/>
			  </form:select>
			 </td>
		  </tr>
		  
		  <tr>
		  	<td>Version:&nbsp;</td>  <%-- Indicates which launch queue to place job request on --%>
			<td>
			  <form:radiobutton path="majorVersion"/>Minor
			  <form:radiobutton path="majorVersion"/>Major
			 </td>
		  </tr>
		  
		  <tr>
		  	<td>Version No:
		  </tr>
		  
		</table>
		<br/>
		<input type="submit" value="Generate"/>
		<input type="submit" value="Cancel" disabled="disabled"/>
		
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
		
	</form:form>
  </div>
</body>
</html>
