<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>

<div class="centerSection">
	<c:set var="DATE_FORMAT" value="<%= CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	<a href="<%=WebConstants.MVC_ADMIN_JURIS_CODE_CREATE%>">Create Jurisdiction Code</a>
	<%-- Table of ContentType --%>
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%= WebConstants.KEY_JURIS_TYPE_CODE %>" class="displayTagTable" cellpadding="2">
	  <display:setProperty name="basic.msg.empty_list">No Jurisdiction codes were found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:column>
	  	<a href="<%=WebConstants.MVC_ADMIN_JURIS_CODE_EDIT%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Edit</a>
	  	<a href="<%=WebConstants.MVC_ADMIN_JURIS_CODE_DELETE%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Delete</a>
	  </display:column>
	  <display:column title="Name" property="name" />
	  <display:column title="Last Updated" >
	  	<fmt:formatDate value="${vdo.lastUpdatedJurisTypeCode}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	</display:table>
</div>