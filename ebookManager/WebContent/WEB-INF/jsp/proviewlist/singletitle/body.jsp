<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus"%>

<script type="text/javascript">
function openFullcreenWindow(url)
{
	window.open(url, "","channelmode,scrollbars,resizable");
}

</script>
	<c:set var="isSuperUser" value="false"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="isSuperUser" value="true"/>
	</sec:authorize>
	<c:set var="isPlusOrSuperUser" value="false"/>
	<sec:authorize access="hasAnyRole('ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
		<c:set var="isPlusOrSuperUser" value="true"/>
	</sec:authorize>
	<c:set var="DATE_FORMAT" value="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS%>"
				   pagesize="10"
				   partialList="true"
				   size="resultSize"
				   >
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  	<display:setProperty name="paging.banner.onepage" value=" " />
	  	<display:column title="Title ID" property="titleId" sortable="true"/>
	  	<display:column title="ProView Display Name" property="title" sortable="true"/>
	  	<display:column title="Latest Version" property="version" sortable="true"/>
	  	<display:column title="Publisher" property="publisher" sortable="true"/>
	  	<display:column title="Last Update" property="lastupdate" sortable="true"/>
	  	<display:column title="Status" property="status" sortable="true"/>
	  	<c:set var="pilotInProgress" value="<%= PilotBookStatus.IN_PROGRESS.toString() %>" />
	  	<c:if test="${isPlusOrSuperUser == 'true'}">
		  	<display:column title="Promote">
		  		<c:choose>
		  			<c:when test="${pilotBookStatus != pilotInProgress}">
		  				<input value="Promote to Final" type="button" <c:if test="${isSplitBook == true}"><c:out value="disabled='disabled'"/></c:if>  onclick="disabled=true; openFullcreenWindow('<%=WebConstants.MVC_PROVIEW_TITLE_PROMOTE%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.titleId}&<%=WebConstants.KEY_VERSION_NUMBER%>=${vdo.version}&<%=WebConstants.KEY_STATUS%>=${vdo.status}&<%=WebConstants.KEY_LAST_UPDATE%>=${vdo.lastupdate}')"/>
		  			</c:when>
		  			<c:otherwise>
		  				Pilot book marked as 'In Progress' for notes migration. Once the note migration csv file is in place, update the Pilot Book status, and regenerate the book before Promoting. 
		  			</c:otherwise>
		  		</c:choose>
		  	</display:column>
		</c:if>
		<c:if test="${ isSuperUser == 'true'}">
		  	<display:column title="Remove">
		  		<input value="Remove" type="button" <c:if test="${isSplitBook == true}"><c:out value="disabled='disabled'"/></c:if> onclick="disabled=true; openFullcreenWindow('<%=WebConstants.MVC_PROVIEW_TITLE_REMOVE%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.titleId}&<%=WebConstants.KEY_VERSION_NUMBER%>=${vdo.version}&<%=WebConstants.KEY_STATUS%>=${vdo.status}&<%=WebConstants.KEY_LAST_UPDATE%>=${vdo.lastupdate}')"/>
		  	</display:column>
		  	<display:column title="Delete">
		  		<input value="Delete" type="button" <c:if test="${isSplitBook == true}"><c:out value="disabled='disabled'"/></c:if> onclick="disabled=true; openFullcreenWindow('<%=WebConstants.MVC_PROVIEW_TITLE_DELETE%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.titleId}&<%=WebConstants.KEY_VERSION_NUMBER%>=${vdo.version}&<%=WebConstants.KEY_STATUS%>=${vdo.status}&<%=WebConstants.KEY_LAST_UPDATE%>=${vdo.lastupdate}')"/>
		  	</display:column>
	  	</c:if>
	</display:table>