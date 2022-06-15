<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@ page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitlesReportFilterForm" %>

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
		commandSelector.val('<%=ProviewTitlesReportFilterForm.Command.REFRESH%>');
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
		action="<%=WebConstants.MVC_PROVIEW_TITLES_REPORT%>"
		modelAttribute="<%=ProviewTitlesReportFilterForm.FORM_NAME%>"
		method="get">

	<form:hidden path="command"/>

	<a id="excelExport" href="<%= WebConstants.MVC_PROVIEW_TITLE_REPORT_DOWNLOAD %>">Download Title Report Excel</a>

	<div class="buttons">
			<input id="refreshButton" type="button" value="Refresh Titles from ProView" onclick="refresh();"/>
	</div>
	
</form:form>
