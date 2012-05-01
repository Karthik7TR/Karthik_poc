<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="centerSection">
	<c:set var="DATE_FORMAT" value="<%= WebConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_CODE_CREATE%>">Create Keyword Code</a>
	<%-- Table of ContentType --%>
	<display:table id="<%= WebConstants.KEY_VDO %>" name="keywordTypeCode" class="displayTagTable">
		<display:setProperty name="basic.msg.empty_list">No Keyword codes were found.</display:setProperty>
		<display:setProperty name="paging.banner.onepage" value=" " />
		<display:column>
			<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_CODE_EDIT%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Edit</a>
			<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_CODE_DELETE%>?<%=WebConstants.KEY_ID%>=${vdo.id}">Delete</a>
		</display:column>
		<display:column title="Name" property="name" />
		<display:column title="Last Updated" >
			<fmt:formatDate value="${vdo.lastUpdated}" pattern="${DATE_FORMAT}"/>
		</display:column>
		<c:set var="values" value="${vdo.values}" />
		<display:column title="Values">
			<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_VALUE_CREATE%>?keywordCodeId=${vdo.id}">Create Value</a>
			<table class="displayTagTable">
					<thead>
						<tr>
							<th></th>
							<th>Value Name</th>
							<th>Last Updated</th>
						</tr>
					</thead>
					<c:forEach items="${values}" var="value" varStatus="status">
						<tbody>
							<c:set var="row" value="${ status.index % 2 == 0 ? 'odd' : 'even' }" />
							<tr class="${row}">
								<td><a href="<%=WebConstants.MVC_ADMIN_KEYWORD_VALUE_EDIT%>?<%=WebConstants.KEY_ID%>=${value.id}">Edit</a>
									<a href="<%=WebConstants.MVC_ADMIN_KEYWORD_VALUE_DELETE%>?<%=WebConstants.KEY_ID%>=${value.id}">Delete</a></td>
								<td>${ value.name }</td>
								<td><fmt:formatDate value="${value.lastUpdated}" pattern="${DATE_FORMAT}"/></td>
							</tr>
						</tbody>
					</c:forEach>
			</table>
		</display:column>
	</display:table>
</div>