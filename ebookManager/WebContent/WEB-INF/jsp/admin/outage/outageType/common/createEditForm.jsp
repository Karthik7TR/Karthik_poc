<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage.OutageTypeForm"%>

<form:form commandName="<%= OutageTypeForm.FORM_NAME %>">
	<%-- Validation Error Message Presentation (if any) --%>
	<spring:hasBindErrors name="<%=OutageTypeForm.FORM_NAME%>">
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
    
    <%-- Informational messages - used to report status of operations. --%>
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
    
	<form:hidden path="outageTypeId" />
	<div class="row">
		<form:label path="system">System</form:label>
		<form:input path="system"/>
	</div>
	<div class="row">
		<form:label path="subSystem">Sub-system</form:label>
		<form:input path="subSystem"/>
	</div>
	<div class="buttons">
		<form:button type="submit">Save</form:button>
		<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST%>';">Cancel</button>
	</div>
</form:form>
