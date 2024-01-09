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
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%= WebConstants.KEY_BOOK_DEFINITION_LOCK %>" class="displayTagTable" cellpadding="2">
	  <display:setProperty name="basic.msg.empty_list">No Book Definitions are locked.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:column>
	  	<a href="<%=WebConstants.MVC_ADMIN_BOOK_LOCK_DELETE%>?<%=WebConstants.KEY_ID%>=${vdo.ebookDefinitionLockId}">Delete</a>
	  </display:column>
	  <display:column title="Title ID" property="ebookDefinition.fullyQualifiedTitleId" />
	  <display:column title="ProView Display Name " property="ebookDefinition.proviewDisplayName" />
	  <display:column title="Full Name" property="fullName" />
	  <display:column title="Username" property="username" />
	  <display:column title="Locked Date/Time" >
	  	<fmt:formatDate value="${vdo.checkoutTimestamp}" pattern="${DATE_FORMAT}"/>
	  </display:column>
	</display:table>
</div>