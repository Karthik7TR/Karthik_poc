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
	<%-- Table of ContentType --%>
	<display:table id="support" name="<%= WebConstants.KEY_SUPPORT %>" class="displayTagTable" cellpadding="2">
	  <display:setProperty name="basic.msg.empty_list">No Support Links were found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:column title="Link Description" property="linkDescription" />
	  <display:column title="Address">
	  	<a href="${support.linkAddress}" target="_blank">${support.linkAddress}</a>
	  </display:column>
	  <display:column title="Last Updated" >
	  	<fmt:formatDate value="${support.lastUpdated}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	</display:table>
</div>