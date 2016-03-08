<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupForm"%>

<html>
<head>
<script type="text/javascript">
		function refresh(){
			$('#command').val('<%=ProviewGroupForm.Command.REFRESH%>');
			$('#<%=ProviewGroupForm.FORM_NAME%>').submit();
			return true; 
		}
		
		function changePageSize(){
			$('#command').val('<%=ProviewGroupForm.Command.PAGESIZE%>');
			$('#<%=ProviewGroupForm.FORM_NAME%>').submit();
			return true; 
		}
</script>		
</head>

	
	
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

	<form:form action="<%=WebConstants.MVC_PROVIEW_GROUPS%>"
			   commandName="<%=ProviewGroupForm.FORM_NAME%>" name="groupForm" method="post">
			   
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
			   
	<display:table id="proviewGroup" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_PROVIEW_GROUPS%>"
				   pagesize="${pageSize}"
				   partialList="false"
				   size="resultSize"
				   export="true"
				   >
				   
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  	<display:setProperty name="paging.banner.onepage" value=" " />
	  	<display:setProperty name="export.xml" value="false" />
	  	<display:setProperty name="export.csv" value="false" />
	  	<display:setProperty name="export.excel.filename" value="ProviewGroup.xls" />
	  	
	  	
	  	
	  	<display:column title="Group Name" property="groupName" sortable="true"/>
	  	<display:column title="Group Title ID" property="groupId" sortable="true"/>
	  	<display:column title="Group Version" property="groupVersion" sortable="true"/>
	  	<display:column title="Group Status" property="groupStatus" />
	  	<display:column title="Action" sortable="false" media="html">
	  			<a href="<%=WebConstants.MVC_PROVIEW_GROUP_BOOK_VERSIONS%>?<%=WebConstants.KEY_GROUP_BY_VERSION_ID%>=${proviewGroup.groupIdByVersion}">View Group details</a>
	  	</display:column>
	  	
	  	
	  	
	</display:table>
	
	
	<div class="buttons">
			<input id="refreshButton" type="button" value="Refresh from ProView" onclick="refresh();"/>
	</div>
	
	
	</form:form>

</html>	