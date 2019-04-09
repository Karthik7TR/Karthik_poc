<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm"%>

<script type="text/javascript">
		function refresh(){
			$('#command').val('<%=ProviewTitleForm.Command.REFRESH%>');
			$('#<%=ProviewTitleForm.FORM_NAME%>').submit();
			return true; 
		}
		
		function changePageSize(){
			$('#command').val('<%=ProviewTitleForm.Command.PAGESIZE%>');
			$('#<%=ProviewTitleForm.FORM_NAME%>').submit();
			return true; 
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
	<c:if test="${errMessage != null}">
		<div class="infoMessageError">
			${errMessage}
		</div>
		<br/>
	</c:if>

	<form:form action="<%=WebConstants.MVC_PROVIEW_TITLES%>"
			   commandName="<%=ProviewTitleForm.FORM_NAME%>" name="theForm" method="post">
			   
	<form:hidden path="command"/>
	
	Items per page:
	<form:select path="objectsPerPage" onchange="changePageSize();">
		<form:option label="20" value="20"/>
		<form:option label="50" value="50"/>
		<form:option label="100" value="100"/>
		<form:option label="250" value="250"/>
		<form:option label="ALL" value="<%= Integer.MAX_VALUE %>"/>
	</form:select>
	<br>
	
	<display:table id="proviewList" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_PROVIEW_TITLES%>"
				   pagesize="${pageSize}"
				   partialList="false"
				   size="resultSize"
				   export="true">
		
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
		<display:setProperty name="paging.banner.onepage" value=" " />
		<display:setProperty name="export.xml" value="false" />
		<display:setProperty name="export.csv" value="false" />
		<display:setProperty name="export.excel.filename" value="ProviewList.xls" />
		
		
		<display:column title="ProView Display Name" property="title" sortable="true"/>
		<display:column title="Title ID" property="titleId" sortable="true"/>
		<display:column title="Total Versions" property="totalNumberOfVersions" sortable="true"/>
		<display:column title="Latest Version" property="version" comparator="com.thomsonreuters.uscl.ereader.deliver.service.VersionComparator" sortable="true"/>
		<display:column title="Status" property="status" sortable="true"/>
		<display:column title="Publisher" property="publisher" sortable="true"/>
		<display:column title="Last Update" property="lastupdate" comparator="com.thomsonreuters.uscl.ereader.deliver.service.LastUpdateComporator" sortable="true"/>
		<display:column title="Latest Status Update" property="lastStatusUpdateDate" comparator="com.thomsonreuters.uscl.ereader.deliver.service.LastUpdateComporator" sortable="true"/>
		<display:column title="Action" sortable="false" media="html">
				<a href="<%=WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS%>?<%=WebConstants.KEY_TITLE_ID%>=${proviewList.titleId}">View all versions</a>
		</display:column>
		
		
	</display:table>
	<a id="excelExport" href="<%= WebConstants.MVC_PROVIEW_TITLE_DOWNLOAD %>">Download Excel</a>
	
	<div class="buttons">
			<input id="refreshButton" type="button" value="Refresh from ProView" onclick="refresh();"/>
	</div>
	
	</form:form>