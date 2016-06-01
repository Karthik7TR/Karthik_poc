<!--
	Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>

	<script type="text/javascript">
		
		function submitForm(){
			document.getElementById('submitStatus').innerHTML = "ProView request submitted... waiting for response."; 			
			$('#<%=ProviewGroupListFilterForm.FORM_NAME%>').submit();
			return true; 
		}
		
		function submitDelete(){
			var confirmed = confirm("Are you sure to Delete?");
			if (confirmed){
				submitForm();
			}
			return confirmed;
		}
		
	</script>
	<style>
		/* fuss with the css to make the table pretty */
		.displayTagTable {
			width: auto;
		}
		.displayTagTable th, .displayTagTable td {
			padding: 2px 30px !important;
		}
	</style>
	<form:form action="<%=WebConstants.MVC_PROVIEW_GROUP_BOOK_DELETE%>"
			   commandName="<%=ProviewGroupListFilterForm.FORM_NAME%>" name="theForm" method="post">
		<td>
				<form:hidden path="bookDefinitionId" value="${bookDefinitionId}"/>
				<form:hidden path="proviewGroupID" value="${proviewGroupID}"/>
				<form:hidden path="groupVersion" value="${groupVersion}"/>
				<form:hidden path="groupIds" value="${groupIds}"/>
				<form:hidden path="groupIdByVersion" value="${groupIdByVersion}"/>
				<form:hidden path="groupName" value="${groupName}"/>
				<form:hidden path="groupOperation" value="${groupOperation}"/>
		</td>
		
		<table>
			<c:choose>
				<c:when test="${proviewGroupListFilterForm.groupOperation}">
					<tr>
						<td id="groupName"><b>Group Name:</b></td> 
						<td>&nbsp;&nbsp;&nbsp;${groupName}</td>
					</tr>
					<tr>
						<td id="status"><b>Group Status:</b></td> 
						<td>&nbsp;&nbsp;&nbsp;${groupStatus}</td>
					</tr>
					<tr>
						<td id="groupVersion"><b>Group Version:</b></td>
						<td>&nbsp;&nbsp;&nbsp;v${ groupVersion }</td>
					</tr>
				</c:when>
				<c:otherwise>
					<td>Group not selected</td>
				</c:otherwise>
			</c:choose>
			<display:table id="groupDetail" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
							requestURI="<%=WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION%>"
							sort="external">
				
				<display:setProperty name="basic.msg.empty_list">No subgroups selected</display:setProperty>
				<display:setProperty name="paging.banner.onepage" value=" " />
				
				<c:choose>
					<c:when test="${showSubgroup}">
					
						<display:column title="Subgroup Name" property="subGroupName" />
					
						<c:set var="values" value="${groupDetail.titleIdList}" />
						<display:column title="Title ID" style="text-align: left">
							<table class="displayTagTable">
									<c:forEach items="${values}" var="value" varStatus="status">
										<tbody>
											<tr >
												<td>${ value }</td>
											</tr>
										</tbody>
									</c:forEach>
							</table>
						</display:column>
					</c:when>
					<c:otherwise>
						<display:column title="Title ID" property= "titleId" />
					</c:otherwise>
				</c:choose>
				
				<display:column title="Proview Display Name" property="proviewDisplayName" />
				<display:column title="Version" property="bookVersion" />
			</display:table>
			<c:if test="${!(empty proviewGroupListFilterForm.groupMembers)}">
				<tr>
					<td><b>Comments (Optional)</b></td>
					<td><form:textarea path="comments" /></td>
				</tr>
			</c:if>
		</table>
		
		<div class="buttons">
			<input id="deleteButton" type="button" <c:if test="${isComplete == true}"><c:out value="disabled='disabled'"/></c:if> value="Delete" onclick="submitDelete()"/>
			<input id="cancelButton" type="button" <c:if test="${isComplete == true}"><c:out value="disabled='disabled'"/></c:if>  onclick=" location.href ='<%=WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION%>?<%=WebConstants.KEY_GROUP_BY_VERSION_ID%>=${groupIdByVersion}';" value="Cancel"/>		
		</div>
		
		<%-- Informational Messages area --%>
		<div id="submitStatus" style="background: yellow;">
		</div>
		<c:if test="${infoMessage != null}">
		<div style="background: lightgreen;">
			${infoMessage}
		</div>
		<br/>
		</c:if>
		<%-- Error Messages area --%>
		<c:if test="${errMessage != null}">
		<div style="background: red;">
			${errMessage}
		</div>
		<br/>
		</c:if>
		
	</form:form>
