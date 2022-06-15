<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@ page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.DisplayTagSortProperty"%>

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
		commandSelector.val('<%=ProviewListFilterForm.Command.REFRESH%>');
		submitLeftFormAndBodyForm();
	}
</script>

	<c:set var="DATE_FORMAT" value="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	
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
		action="<%=WebConstants.MVC_PROVIEW_TITLES%>"
		modelAttribute="<%=ProviewListFilterForm.FORM_NAME%>"
		method="get">

	<form:hidden path="command"/>
	<jsp:include page="../tableLegend.jsp"/>
	Items per page:
	<form:select path="objectsPerPage" onchange="submitLeftFormAndBodyForm();">
		<form:option label="${ defaultPageSize }" value="${ defaultPageSize }"/>
		<form:option label="50" value="50"/>
		<form:option label="100" value="100"/>
		<form:option label="250" value="250"/>
		<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
	</form:select>
	<br>

	<form:hidden path="sort" value="${ param.sort }"/>
	<form:hidden path="dir" value="${ param.dir }"/>

	<display:table id="proviewList" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2"
				   requestURI="<%=WebConstants.MVC_PROVIEW_TITLES%>"
				   export="true"
					sort="external">
		
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
		<display:setProperty name="paging.banner.onepage" value=" " />
		<display:setProperty name="export.xml" value="false" />
		<display:setProperty name="export.csv" value="false" />
		<display:setProperty name="export.excel.filename" value="ProviewList.xls" />
		
		
		<display:column title="ProView Display Name" property="title" sortable="true" sortProperty="<%=DisplayTagSortProperty.PROVIEW_DISPLAY_NAME.toString() %>"/>
		<display:column title="Title ID" property="titleId" sortable="true" sortProperty="<%=DisplayTagSortProperty.TITLE_ID.toString() %>"/>
		<display:column title="Total Versions" property="totalNumberOfVersions" sortable="true" sortProperty="<%=DisplayTagSortProperty.TOTAL_VERSIONS.toString() %>"/>
		<display:column title="Split Parts" sortable="true" sortProperty="<%=DisplayTagSortProperty.SPLIT_PARTS.toString() %>">
			${proviewList.splitParts.size()}
		</display:column>
		<display:column title="Latest Version" property="version" sortProperty="<%=DisplayTagSortProperty.LATEST_VERSION.toString() %>" sortable="true"/>
		<display:column title="Status" property="status" sortable="true" sortProperty="<%=DisplayTagSortProperty.STATUS.toString() %>"/>
		<display:column title="Publisher" property="publisher" sortable="true" sortProperty="<%=DisplayTagSortProperty.PUBLISHER.toString() %>"/>
		<display:column title="Last Update" property="lastupdate" sortProperty="<%=DisplayTagSortProperty.LAST_UPDATE.toString() %>" sortable="true"/>
		<display:column title="Latest Status Update" property="lastStatusUpdateDate" sortProperty="<%=DisplayTagSortProperty.LATEST_STATUS_UPDATE.toString() %>" sortable="true"/>
		<display:column title="Action" sortable="false" media="html">
				<a href="<%=WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS%>?<%=WebConstants.KEY_TITLE_ID%>=${proviewList.titleId}">View all versions</a>
		</display:column>
		
		
	</display:table>
	<a id="excelExport" href="<%= WebConstants.MVC_PROVIEW_TITLE_DOWNLOAD %>">Download Excel</a>

	<div class="buttons">
			<input id="refreshButton" type="button" value="Refresh from ProView" onclick="refresh();"/>
	</div>
	
	</form:form>
