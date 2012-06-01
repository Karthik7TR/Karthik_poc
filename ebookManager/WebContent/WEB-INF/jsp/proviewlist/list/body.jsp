<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm"%>

<html>
<head>
<script type="text/javascript">
		function submitForm(cmd){
			$('#<%=ProviewTitleForm.FORM_NAME%>').submit();
			return true; 
		}
</script>		
</head>

	<c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>

	<form:form action="<%=WebConstants.MVC_PROVIEW_TITLES%>"
			   commandName="<%=ProviewTitleForm.FORM_NAME%>" name="theForm" method="post">
	
	<display:table id="proviewList" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_PROVIEW_TITLES%>"
				   pagesize="20"
				   partialList="false"
				   size="resultSize"
				   export="true"
				   >
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  	<display:setProperty name="paging.banner.onepage" value=" " />
	  	<display:setProperty name="export.xml" value="false" />
	  	<display:setProperty name="export.csv" value="false" />
	  	<display:setProperty name="export.excel.filename" value="ProviewList.xls" />
	  	
	  	
	  	
	  	<display:column title="Title ID" property="titleId" sortable="true"/>
	  	<display:column title="Title" property="title" sortable="true"/>
	  	<display:column title="Total Versions" property="totalNumberOfVersions" sortable="true"/>
	  	<display:column title="Latest Version" property="version" sortable="true"/>
	  	<display:column title="Status" property="status" sortable="true"/>
	  	<display:column title="Publisher" property="publisher" sortable="true"/>
	  	<display:column title="Last Update" property="lastupdate" sortable="true"/>
	  	<display:column title="Action" sortable="true" media="html" sortProperty="totalNumberOfVersions">
	  			<a href="<%=WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS%>?<%=WebConstants.KEY_TITLE_ID%>=${proviewList.titleId}">View all versions</a>
	  	</display:column>
	  	
	  	
	  	
	  	
	</display:table>
	
	<div class="buttons">
			<input id="refreshButton" type="button" value="Refresh from ProView" onclick="submitForm();"/>
	</div>
	
	</form:form>

</html>	