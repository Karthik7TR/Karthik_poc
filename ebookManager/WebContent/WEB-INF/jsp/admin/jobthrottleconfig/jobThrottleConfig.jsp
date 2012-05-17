<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig.JobThrottleConfigForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.job.domain.AppConfig.Key"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig.JobThrottleConfigController"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<form:form action="<%=WebConstants.MVC_ADMIN_JOB_THROTTLE_CONFIG %>"
		   commandName="<%=JobThrottleConfigForm.FORM_NAME%>" method="post">
		   
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=JobThrottleConfigForm.FORM_NAME%>">
		<div class="errorBox">
	      <b><spring:message code="please.fix.errors"/>:</b><br/>
	      <form:errors path="*">
	      	<ul>
			<c:forEach items="${messages}" var="message">
				<li style="color: black">${message}</li>
			</c:forEach>
	      	</ul>
		  </form:errors>
		  <br/>
	    </div>
    </spring:hasBindErrors>
    
    <%-- Informational messages - used to report status of job stop and/or restart operations. --%>
    <c:if test="${fn:length(infoMessages) > 0}">
 	<ul>
 	<c:forEach items="${infoMessages}" var="message">
 		<c:if test="${message.type == 'SUCCESS'}">
 			<c:set var="cssStyle" value="color:darkgreen;"/>
 		</c:if>
 		 <c:if test="${message.type == 'FAIL' || message.type == 'ERROR'}">
 			<c:set var="cssStyle" value="color:red;"/>
 		</c:if>
		<li style="${cssStyle}">${message.text}</li>
	</c:forEach>
 	</ul>
    </c:if>	   
		   
	Core Thread pool size: 
	<form:select path="<%=Key.coreThreadPoolSize.toString()%>">
		<form:option label="0" value="0"/>
		<form:option label="1" value="1"/>
		<form:option label="2" value="2"/>
		<form:option label="3" value="3"/>
		<form:option label="4" value="4"/>
		<form:option label="5" value="5"/>
		<form:option label="6" value="6"/>
		<form:option label="7" value="7"/>
		<form:option label="8" value="8"/>
	</form:select><br/>
	<br/>
	
	Step throttle enabled:
	<form:radiobutton path="<%=Key.stepThrottleEnabled.toString()%>" label="True" value="<%=Boolean.TRUE%>"/>
	<form:radiobutton path="<%=Key.stepThrottleEnabled.toString()%>" label="False" value="<%=Boolean.FALSE%>"/><br/>
	<br/>
	
	Throttle step name:
	<form:select path="<%=Key.throttleStepName.toString()%>">
		<form:option label="-- Select --" value=""/>
		<form:options items="${stepNames}"/>
	</form:select><br/>
	<br/>
	
	Throttle step maximum jobs: 
	<form:select path="<%=Key.throtttleStepMaxJobs.toString()%>">
		<form:option label="0" value="0"/>
		<form:option label="1" value="1"/>
		<form:option label="2" value="2"/>
		<form:option label="3" value="3"/>
		<form:option label="4" value="4"/>
		<form:option label="5" value="5"/>
		<form:option label="6" value="6"/>
		<form:option label="7" value="7"/>
		<form:option label="8" value="8"/>
	</form:select><br/>
	<br/>
<div class="buttons">
		<form:button type="submit" id="Save">Save</form:button>
		&nbsp;
		<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_MAIN%>';">Cancel</button>
</div>

</form:form>