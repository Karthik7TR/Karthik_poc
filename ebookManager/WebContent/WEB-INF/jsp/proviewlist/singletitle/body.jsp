<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<html>
<head>
<script type="text/javascript">
function openFullcreenWindow(url)
{
	window.open(url, "","channelmode,scrollbars");
}

</script>
</head>
	<c:set var="isSuperUser" value="false"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="isSuperUser" value="true"/>
	</sec:authorize>
	<c:set var="isPlusOrSuperUser" value="false"/>
	<sec:authorize access="hasAnyRole('ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
		<c:set var="isPlusOrSuperUser" value="true"/>
	</sec:authorize>
	<c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_PROVIEW_TITLES%>"
				   pagesize="10"
				   partialList="true"
				   size="resultSize"
				   >
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  	<display:setProperty name="paging.banner.onepage" value=" " />
	  	<display:column title="Title ID" property="titleId" sortable="true"/>
	  	<display:column title="Title" property="title" sortable="true"/>
	  	<display:column title="Latest Version" property="version" sortable="true"/>
	  	<display:column title="Publisher" property="publisher" sortable="true"/>
	  	<display:column title="Last Update" property="lastupdate" sortable="true"/>
	  	<display:column title="Status" property="status" sortable="true"/>
	  	<c:if test="${ isPlusOrSuperUser == 'true' }">
		  	<display:column title="Promote">
		  		<input value="Promote to Final" type="button" onclick="disabled=true; openFullcreenWindow('<%=WebConstants.MVC_PROVIEW_TITLE_PROMOTE%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.titleId}&<%=WebConstants.KEY_VERSION_NUMBER%>=${vdo.version}&<%=WebConstants.KEY_STATUS%>=${vdo.status}')"/>
		  	</display:column>
		</c:if>
		<c:if test="${ isSuperUser == 'true' }">
		  	<display:column title="Remove">
		  		<input value="Remove" type="button" onclick="disabled=true; openFullcreenWindow('<%=WebConstants.MVC_PROVIEW_TITLE_REMOVE%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.titleId}&<%=WebConstants.KEY_VERSION_NUMBER%>=${vdo.version}&<%=WebConstants.KEY_STATUS%>=${vdo.status}')"/>
		  	</display:column>
		  	<display:column title="Delete">
		  		<input value="Delete" type="button" onclick="disabled=true; openFullcreenWindow('<%=WebConstants.MVC_PROVIEW_TITLE_DELETE%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.titleId}&<%=WebConstants.KEY_VERSION_NUMBER%>=${vdo.version}&<%=WebConstants.KEY_STATUS%>=${vdo.status}')"/>
		  	</display:column>
	  	</c:if>
	</display:table>
	
</html>	