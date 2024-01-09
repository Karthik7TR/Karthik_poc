<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<script type="text/javascript" src="js/shared/dateUtils.js"></script>
<script>
$(document).ready(function(){
	formatDates();
})
</script>
<div class="centerSection">
	<%-- Informational messages --%>
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
    
	<c:set var="DATE_FORMAT" value="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	<a href="<%=WebConstants.MVC_ADMIN_OUTAGE_CREATE%>">Create Outage Schedule</a>
	<br/>
	<%-- Table of ContentType --%>
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%= WebConstants.KEY_OUTAGE %>" class="displayTagTable" cellpadding="2"
		requestURI="<%=WebConstants.MVC_ADMIN_OUTAGE_ACTIVE_LIST%>"
		pagesize="20"
		partialList="false"
		defaultsort="3"
		defaultorder="descending">
	  <display:setProperty name="basic.msg.empty_list">No outage schedule was found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:column>
	  	<a href="<%=WebConstants.MVC_ADMIN_OUTAGE_EDIT%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Edit</a>
	  	<a href="<%=WebConstants.MVC_ADMIN_OUTAGE_DELETE%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Delete</a>
	  </display:column>
	  <display:column title="Outage Type" >
	  	System: ${ vdo.outageType.system } <br/>
	  	Sub-system: ${ vdo.outageType.subSystem }
	  </display:column>
	  <display:column title="Start Date/Time" sortable="true" class="toFormatDate">
	  	${ vdo.startTime.toInstant() }
	  </display:column>
	  <display:column title="End Date/Time" sortable="true" class="toFormatDate">
	  	${ vdo.endTime.toInstant() }
	  </display:column>
	  <display:column title="Reason" property="reason" />
	  <display:column title="System Impact Description" property="systemImpactDescription"/>
	  <display:column title="Servers Impacted" property="serversImpacted" />
	  <display:column title="Notification Email Sent?" property="notificationEmailSent" />
	  <display:column title="All Clear Email Sent?" property="allClearEmailSent" />
	  <display:column title="Updated By" property="updatedBy" sortable="true"/>
	  <display:column title="Last Updated" sortable="true" class="toFormatDate">
	  	${ vdo.lastUpdated.toInstant()}
	  </display:column>
	</display:table>
</div>