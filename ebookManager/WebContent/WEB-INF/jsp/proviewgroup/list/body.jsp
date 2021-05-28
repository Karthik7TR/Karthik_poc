<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupForm"%>

<c:set var="defaultPageSize" value="<%=WebConstants.DEFAULT_PAGE_SIZE%>"/>

<script type="text/javascript" src="js/form-utils.js"></script>
<script type="text/javascript">

	$(document).ready(function () {
		$('#command').prop('disabled', true);
	});

	const opp = "${ pageSize == null ? defaultPageSize : pageSize }";
	$(window).on('pageshow', function() {
		$('#objectsPerPage option[value=' + opp + ']').prop('selected', true);
	});

	function refresh() {
		const commandSelector = $('#command');
		commandSelector.prop('disabled', false);
		commandSelector.val('<%=ProviewGroupForm.Command.REFRESH%>');
		submitLeftFormAndBodyForm();
	}

</script>
	
	<%-- Informational Messages area --%>
	<c:if test="${infoMessage != null}">
		<div class="infoMessageSuccess">
			${infoMessage}
		</div>
		<br/>
	</c:if>
	<%-- Error Messages area --%>
	<c:if test="${errorOccurred != null}">
		<div class="infoMessageError">
			ProView Exception occurred. Please contact your administrator.
		</div>
		<br/>
	</c:if>

<form:form
		id="bodyForm"
		modelAttribute="<%=ProviewGroupForm.FORM_NAME%>"
		action="<%=WebConstants.MVC_PROVIEW_GROUPS%>"
		method="get">

	<form:hidden path="command"/>

	Items per page:
	<form:select path="objectsPerPage" onchange="submitLeftFormAndBodyForm();">
		<form:option label="${ defaultPageSize }" value="${ defaultPageSize }"/>
		<form:option label="50" value="50"/>
		<form:option label="100" value="100"/>
		<form:option label="250" value="250"/>
		<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
	</form:select>
	<br>
	
	<display:table id="proviewGroup" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_PROVIEW_GROUPS%>"
				   pagesize="${pageSize}"
				   partialList="false"
				   size="resultSize"
				   export="true">
		
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
		<display:setProperty name="paging.banner.onepage" value=" " />
		
		
		<display:column title="Group Name" property="groupName" sortable="true" comparator="com.thomsonreuters.uscl.ereader.mgr.web.CaseSensetiveStringComporator"/>
		<display:column title="Group ID" property="groupId" sortable="true"/>
		<display:column title="Latest Status" property="groupStatus" sortable="true"/>
		<display:column title="Total Versions" property="totalNumberOfVersions" sortable="true"/>
		<display:column title="Latest Version" property="groupVersion" sortable="true" comparator="com.thomsonreuters.uscl.ereader.deliver.service.VersionComparator"/>
		<display:column title="Latest Status Update" property="latestUpdateDate" comparator="com.thomsonreuters.uscl.ereader.deliver.service.LastUpdateComporator" sortable="true"/>
		<display:column title="Action" sortable="false" media="html">
			<a id="viewAllVersions" href="<%=WebConstants.MVC_PROVIEW_GROUP_ALL_VERSIONS%>?<%=WebConstants.KEY_GROUP_IDS%>=${proviewGroup.groupId}">View all versions</a>
		</display:column>
		
	</display:table>
	<a id="excelExport" href="<%= WebConstants.MVC_PROVIEW_GROUP_DOWNLOAD %>">Download Excel</a>
	
	<div class="buttons">
			<input id="refreshButton" type="button" value="Refresh from ProView" onclick="refresh();"/>
	</div>

</form:form>
