<!--
	Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->


<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.GroupCmd"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="org.springframework.batch.core.BatchStatus"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus"%>

<script>
$(document).ready(function() {
	$('#selectAll').click(function () {
		$(this).parents('#<%= WebConstants.KEY_GROUP_DETAIL %>').find(':checkbox').attr('checked', this.checked);
	});
});

function submitGroupForm(command) {
		var confirmed = false;		// confirm with user via pop-up
		var selected = false;		// any subgroups selected?
		var group = true;
		if( document.getElementById('groupChecked') != undefined) {
			group = document.getElementById('groupChecked').checked;
		} else {
			selected = true;
		}
		
		var isReview = true;		// has a book or group of indicated status been selected?
		var isRemove = false;
		
		var subgroups = new Array();
		<c:forEach items="${paginatedList}" var="subgroup">
			groupDetails = new Object();
			//add version number
			groupDetails.status ="${subgroup.bookStatus}";
			subgroups.push(groupDetails);
		</c:forEach>
		
		var x = document.getElementsByName('groupMembers');
		var formatedcommand = command.charAt(0).toUpperCase() + command.slice(1).toLowerCase();
		var groupStatus = document.theForm.elements["groupStatus"].value;
		for (i=0;i<x.length;i++){
			if(x[i].checked){
				selected=true;
				if(subgroups[i].status.toUpperCase() != "REVIEW"){
					isReview = false;
				}
				if(subgroups[i].status.toUpperCase() == "REMOVE"){
					isRemove = true;
				}
			}
		}
		if (selected || group){
			if (group){
				isReview = isReview && (groupStatus == "Review");
			}
			
			if (!isReview && (formatedcommand=="Promote")){
				alert("All selected items must have Status: Review.");
				return;
			} else if (!isRemove && (formatedcommand=="Delete")){
				alert("All selected items must have Status: Remove.");
				return;
			}
		
			if (!group && selected){
				confirmed = confirm("Group not checked: Are you sure you want to " + formatedcommand + " eBooks separately?");
				if (!confirmed){
					return;
				}
			} else if (group && !selected){
				confirmed = confirm("You have selected the Group box with no corresponding eBook titles checked.  Do you want to proceed?")
				if (!confirmed){
					return;
				}
			}
		}
		$("#groupCmd").val(command);  // Set the form hidden field value for the operation discriminator
		
		$("#multiSelectForm").submit();	// POST the HTML form with the selected versions
	
}

</script>
<form:form id="multiSelectForm" action="<%=WebConstants.MVC_PROVIEW_GROUP_OPERATION%>"
			commandName="<%=ProviewGroupListFilterForm.FORM_NAME%>" name="theForm" method="post">
			<form:hidden path="groupCmd"/>
			<form:hidden path="groupName" value="${groupName}"/>
			<form:hidden path="groupStatus" value="${groupStatus}"/>
			<form:hidden path="bookDefinitionId" value="${bookDefinitionId}"/>
			<form:hidden path="proviewGroupID" value="${proviewGroupID}"/>
			<form:hidden path="groupVersion" value="${groupVersion}"/>
		
	
	<spring:hasBindErrors name="<%=ProviewGroupListFilterForm.FORM_NAME%>">
		<b><spring:message code="please.fix.errors"/>:</b><br/>
			<font color="red">	
				<c:forEach var="error" items="${errors.allErrors}">
					<b><spring:message message="${error}" /></b>
					<br/>
				</c:forEach>
			</font>
	</spring:hasBindErrors>
	
	<c:if test="${errMessage != null}">
		<div class="infoMessageError">
			${errMessage}
		</div>
		<br>
	</c:if>
	<c:set var="isSuperUser" value="false"/>
		<sec:authorize access="hasRole('ROLE_SUPERUSER')">
			<c:set var="isSuperUser" value="true"/>
		</sec:authorize>
	<c:set var="isPlusOrSuperUser" value="false"/>
		<sec:authorize access="hasAnyRole('ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
			<c:set var="isPlusOrSuperUser" value="true"/>
		</sec:authorize>
	<c:set var="pilotInProgress" value="<%= PilotBookStatus.IN_PROGRESS.toString() %>" />
	
	<div class="row" id ="groupName">
						<label class="labelCol" >Group Name:</label>
						<span class="field">${ groupName }</span>
	
	</div>
	<div class="row" id ="groupId">
						<label class="labelCol" >Group ID:</label>
						<span class="field">${ proviewGroupID }</span>
	
	</div>
	<div class="row" id ="headTitle">
						<label class="labelCol" >Group Head Title:</label>
						<span class="field">${ headTitle }</span>
	
	</div>
	<div class="row" id="groupSatus">
						<label class="labelCol">Group Status:</label>
						<span class="field">${ groupStatus }</span>
	
	</div>	
	<div class="row" id="groupVersion">
						<label class="labelCol">Group Version:</label>
						<span class="field">v${ groupVersion }</span>
	</div>	
	<c:choose>
		<c:when test="${resultSize != 0}">
			<div class="row" id ="groupOp">
				<form:label path="groupOperation">Group:</form:label>
				<form:checkbox path="groupOperation" id='groupChecked' value='false'/>
			</div>
		</c:when>
		<c:otherwise>
			<form:hidden path="groupOperation" value='true'/>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${warningMessage != null}">
	    <div class="infoMessageWarning">
	    	${warningMessage}
	    </div>
	    <br/> 
	    </c:if>
	
	<c:set var="selectAllElement" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<display:table id="groupDetail" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
					requestURI="<%=WebConstants.MVC_PROVIEW_GROUP_SINGLE_VERSION%>"
					>
		
		<display:setProperty name="basic.msg.empty_list">
			<c:if test="${warningMessage != null }">
				<div class="errorMessage">
					<br/><b>No information found in ProView for titles:</b><br/>
					<c:forEach items="${warningMessage}" var="title" varStatus="status">
						"${title}"<br/>
					</c:forEach>					
				</div>
				<br>
			</c:if>
		</display:setProperty>
		<display:setProperty name="paging.banner.onepage" value=" " />
		
		<display:column title="${selectAllElement}"  style="text-align: center">
			<form:checkbox path="groupMembers" value="${groupDetail.idWithVersion}" />
		</display:column>
		<c:choose>
		
			<c:when test="${showSubgroup}">
				<display:column title="Subgroup Name" property="subGroupName" />
				<display:column title="Proview Display Name" property="proviewDisplayName" />
				<c:set var="values" value="${groupDetail.titleInfoList}" />
				<display:column title="Title ID" style="text-align: left">
					<table class="displayTagTable">
							<c:forEach items="${ values }" var="value" varStatus="status">
								<tbody>
									<tr><td>${ value.titleId }</td></tr>
								</tbody>
							</c:forEach>
					</table>
				</display:column>
				<display:column title="Book Status" style="text-align: left">
					<table class="displayTagTable">
						<c:forEach items="${values}" var="value" varStatus="status">
							<tbody>
								<tr><td>${ value.status }</td></tr>
							</tbody>
						</c:forEach>
					</table>
				</display:column>
			</c:when>
			
			<c:otherwise>
				<display:column title="Proview Display Name" property="proviewDisplayName" />
				<display:column title="Title ID" property= "titleId" />
				<display:column title="Book Status" property="bookStatus" />
			</c:otherwise>
			
		</c:choose>
		
		<display:column title="Version" property="bookVersion" />
		<display:column title="Last Update" property="lastupdate" />
	</display:table>
	
	<c:set var="disableButtons" value="disabled"/>
	<sec:authorize access="hasAnyRole('ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
		<c:set var="disableButtons" value=""/>
	</sec:authorize>
	<c:set var="disableRemoveButtons" value="disabled"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="disableRemoveButtons" value=""/>
	</sec:authorize>
	
	<div class="buttons">
		<input id="promoteButton" type="button" ${disableButtons} value="Promote to Final" onclick="submitGroupForm('<%=GroupCmd.PROMOTE%>')"/> &nbsp;
		<input id="removeButton" type="button" ${disableRemoveButtons} value="Remove" onclick="submitGroupForm('<%=GroupCmd.REMOVE%>')"/>&nbsp;
		<c:if test="${resultSize != 0}">
			<input id="deleteButton" type="button" ${disableRemoveButtons} value="Delete" onclick="submitGroupForm('<%=GroupCmd.DELETE%>')"/>
		</c:if>
	</div>
</form:form>
