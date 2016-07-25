<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment"%>
<%@page import="org.apache.log4j.Level" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc.MiscConfigForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc.MiscConfigController"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>

<form:form action="<%=WebConstants.MVC_ADMIN_MISC %>"
		   commandName="<%=MiscConfigForm.FORM_NAME%>" method="post">
		   
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=MiscConfigForm.FORM_NAME%>">
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

	<table>
	<tr>
	<td>App Logger Level:</td>
	<td> 
	<form:select path="<%=MiscConfig.Key.appLogLevel.toString()%>">
		<form:option label="Debug" value="<%=Level.DEBUG.toString()%>"/>
		<form:option label="Info" value="<%=Level.INFO.toString()%>"/>
		<form:option label="Warn" value="<%=Level.WARN.toString()%>"/>
		<form:option label="Error" value="<%=Level.ERROR.toString()%>"/>
		<form:option label="Fatal" value="<%=Level.FATAL.toString()%>"/>
		<form:option label="Off" value="<%=Level.OFF.toString()%>"/>
	</form:select>
	</td>
	</tr>
	<tr>
	<td>Root Loggger Level:</td>
	<td> 
	<form:select path="<%=MiscConfig.Key.rootLogLevel.toString()%>">
		<form:option label="Debug" value="<%=Level.DEBUG.toString()%>"/>
		<form:option label="Info" value="<%=Level.INFO.toString()%>"/>
		<form:option label="Warn" value="<%=Level.WARN.toString()%>"/>
		<form:option label="Error" value="<%=Level.ERROR.toString()%>"/>
		<form:option label="Fatal" value="<%=Level.FATAL.toString()%>"/>
		<form:option label="Off" value="<%=Level.OFF.toString()%>"/>
	</form:select>
	</td>
	</tr>
	<tr>
		<td>Novus Environment:</td>
		<td>
			<form:select path="<%=MiscConfig.Key.novusEnvironment.toString()%>">
				<form:option label="<%=NovusEnvironment.Client.toString()%>" value="<%=NovusEnvironment.Client.toString()%>"/>
				<form:option label="<%=NovusEnvironment.Prod.toString()%>" value="<%=NovusEnvironment.Prod.toString()%>"/>
			</form:select>
		</td>
	</tr>
	<tr>
		<td>ProView Host:</td>
		<td><form:input path="proviewHostname" size="48"/></td>
	</tr>
	<tr>
		<td>Disable Split Book for Existing Single Titles:</td>
		<td>
			<form:radiobutton path="<%=MiscConfig.Key.disableExistingSingleTitleSplit.toString()%>" label="True" value="<%=Boolean.TRUE%>"/>
			<form:radiobutton path="<%=MiscConfig.Key.disableExistingSingleTitleSplit.toString()%>" label="False" value="<%=Boolean.FALSE%>"/>
		</td>
	</tr>
	<tr>
		<td>Maximum numbers of split parts:</td>
		<td><form:input path="<%=MiscConfig.Key.maxSplitParts.toString()%>" size="2"/></td>
	</tr>
	</table>		   
	
<div class="buttons">
		<form:button type="submit" id="Save">Save</form:button>
		&nbsp;
		<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_MAIN%>';">Cancel</button>
</div>

</form:form>