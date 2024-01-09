<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>

<div class="centerSection">
	<%-- Table of ContentType --%>
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%= WebConstants.KEY_DOC_TYPE_CODE %>" class="displayTagTable">
	 <display:setProperty name="basic.msg.empty_list">No Document Type Codes were found.</display:setProperty>
	 <display:setProperty name="paging.banner.onepage" value=" " />
		<display:column>
			<a href="<%=WebConstants.MVC_ADMIN_DOCTYPE_METRIC_EDIT%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Edit</a>
		</display:column>
		<display:column title="Name" property="name" />
		<display:column title="Threshold Value" property="thresholdValue" />
		<display:column title="Threshold Percent" property="thresholdPercent" />
	</display:table>
</div>