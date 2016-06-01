<!--
	Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->


<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


	<c:set var="isSuperUser" value="false"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="isSuperUser" value="true"/>
	</sec:authorize>
	<c:set var="isPlusOrSuperUser" value="false"/>
	<sec:authorize access="hasAnyRole('ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
		<c:set var="isPlusOrSuperUser" value="true"/>
	</sec:authorize>
	<c:set var="DATE_FORMAT" value="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	
	<display:table id="proviewGroup" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS%>"
				   pagesize="10"
				   partialList="true"
				   size="resultSize"
				   >
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  	<display:setProperty name="paging.banner.onepage" value=" " />
	  	<display:column title="Group Name" property="groupName" sortable="true"/>
	  	<display:column title="Group ID" property="groupId" sortable="true"/>
	  	<display:column title="Latest Version" property="groupVersion" sortable="true"/>
	  	<display:column title="Group Status" property="groupStatus" sortable="true"/>
		<display:column title="Action" sortable="false" media="html">
					<a href="<%=WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION%>?<%=WebConstants.KEY_GROUP_BY_VERSION_ID%>=${proviewGroup.groupIdByVersion}">View Group details</a>
		</display:column>
	</display:table>