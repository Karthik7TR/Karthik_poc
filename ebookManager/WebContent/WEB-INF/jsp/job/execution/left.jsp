<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.job.details.JobExecutionForm"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form action="<%=WebConstants.MVC_JOB_EXECUTION_DETAILS_POST%>"
			   	   commandName="<%=JobExecutionForm.FORM_NAME%>" name="theForm" method="post">
			   	   
	<%-- Error Message Presentation --%>
	<spring:hasBindErrors name="<%=JobExecutionForm.FORM_NAME%>">
		<div class="errorBox">
	      <form:errors path="*">
			<br/>
			<c:forEach items="${messages}" var="message">
				${message}<br/>
			</c:forEach>
		  </form:errors>
		  <br/>
	    </div>
	</spring:hasBindErrors>
	    
  	Job Execution ID<br/>
  	<form:input path="jobExecutionId"/>
  	<input type="submit" value="Find"/> &nbsp;
</form:form>