<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>



	<c:set var="DATE_FORMAT" value="<%=WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLES%>"
				   pagesize="20"
				   partialList="true"
				   size="resultSize"
				   >
				   
	
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  	<display:setProperty name="paging.banner.onepage" value=" " />
	  	<display:column title="Title ID" property="titleId" sortable="true"/>
	  	<display:column title="Title" property="title" sortable="true"/>
	  	<display:column title="Latest Version" property="vesrion" sortable="true"/>
	  	<display:column title="Publisher" property="publisher" sortable="true"/>
	  	<display:column title="Last Update" property="lastupdate" sortable="true"/>
	  	<display:column title="Status" property="status" sortable="true"/>
	  	<display:column title="Action">
	  			<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLE_ALL_VERSIONS%>?<%=WebConstants.KEY_TITLE_ID%>=${vdo.titleId}">All versions of this book</a>
	  	</display:column>
	  	
	  	
	</display:table>