<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="centerSection">
	<c:set var="DATE_FORMAT" value="<%= WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	<a href="<%=WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_CREATE%>">Create Publish Type Code</a>
	<%-- Table of ContentType --%>
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%= WebConstants.KEY_PUB_TYPE_CODE %>" class="displayTagTable" cellpadding="2">
	  <display:setProperty name="basic.msg.empty_list">No Publish Type Codes were found.</display:setProperty>
	
	  <display:column>
	  	<a href="<%=WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_EDIT%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Edit</a>
	  	<a href="<%=WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_DELETE%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Delete</a>
	  </display:column>
	  <display:column title="Name" property="name" />
	  <display:column title="Last Updated" >
	  	<fmt:formatDate value="${vdo.lastUpdated}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	</display:table>
</div>