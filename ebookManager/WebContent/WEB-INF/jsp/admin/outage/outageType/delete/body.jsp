<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage.OutageTypeForm"%>

<%-- Check if there is a model to render, if not display error message --%>
<c:choose>
	<c:when test="${outage != null}">
		<c:choose>
			<c:when test="${numberOfPlannedOutages > 0}">
				<div>Following Planned Outages are using this Outage Type.  This Outage Type cannot be deleted.</div>
				
				<%-- Table of ContentType --%>
				<c:set var="DATE_FORMAT" value="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
				<display:table id="<%= WebConstants.KEY_VDO %>"  name="<%= WebConstants.KEY_PLANNED_OUTAGE_TYPE %>"  class="displayTagTable" cellpadding="2"
					pagesize="20"
					partialList="false">
				  <display:setProperty name="basic.msg.empty_list">No Planned Outages.</display:setProperty>
				  <display:setProperty name="paging.banner.onepage" value=" " />
				  <display:column title="Start Date/Time" >
				  	<fmt:formatDate value="${vdo.startTime}" pattern="${DATE_FORMAT}"/>
				  </display:column>
				  <display:column title="End Date/Time" >
				  	<fmt:formatDate value="${vdo.endTime}" pattern="${DATE_FORMAT}"/>
				  </display:column>
				  <display:column title="Reason" property="reason" />
				  <display:column title="System Impact Description" property="systemImpactDescription" />
				  <display:column title="Servers Impacted" property="serversImpacted" />
				  <display:column title="Notification Email Sent?" property="notificationEmailSent" />
				  <display:column title="All Clear Email Sent?" property="allClearEmailSent" />
				  <display:column title="Updated By" property="updatedBy" />
				  <display:column title="Last Updated" >
				  	<fmt:formatDate value="${vdo.lastUpdated}" pattern="${DATE_FORMAT}"/>
				  </display:column>
				</display:table> 
			</c:when>
			<c:otherwise>
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
					Are you sure you want to delete this outage type: 
					<div>
						System ${ outage.system }
					</div>
					<div>
						Sub-system ${ outage.subSystem }
					</div>
					<div class="buttons">
						<form:button id="delete">Delete</form:button>
						<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_OUTAGE_TYPE_LIST%>';">Cancel</button>
					</div>
				</form:form>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<div class="errorMessage">No outage type found</div>
	</c:otherwise>
</c:choose>

