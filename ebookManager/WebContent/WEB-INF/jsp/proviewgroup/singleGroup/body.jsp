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
		var warn = false;			// whether the user should be asked for confirmation
		
		var isReview = true;		// has a book or group of indicated status been selected?
		var isRemove = false;
		var isDelete = false;
		
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
				if(subgroups[i].status.toUpperCase() == "DELETE"){
					isDelete = true;
				}
			}
		}

		if (document.getElementById('groupChecked').checked){
			isReview = isReview && (groupStatus == "Review");
		}
		
		if(isDelete){
			alert("Cannot operate on an item with Status: Delete.");
			return;
		}
		if(!isReview&&(formatedcommand=="Promote")){
			alert("All selected items must have Status: Review.");
			return;
		}
		if(isRemove&&(formatedcommand=="Remove")){
			alert("Cannot remove an item with Status: Remove.");
			return;
		}
		if(!isRemove&&(formatedcommand=="Delete")){
			alert("All selected items must have Status: Remove.");
			return;
		}
		
		warn = !document.getElementById('groupChecked').checked
					&& selected
					&& (groupStatus=="Review"
						|| (command=="REMOVE" && (groupStatus!="Remove"))
						|| (command=="DELETE" && (groupStatus=="Final")));
		if (warn){
			confirmed = confirm("Group not checked: Are you sure you want to " + formatedcommand + " eBooks separately?");
			if (!confirmed){
				return;
			}
		}
		warn = document.getElementById('groupChecked').checked
				&& !selected;
		if(warn){
			confirmed = confirm("You have selected the Group box with no corresponding eBook titles checked.  Do you want to proceed?")
			if (!confirmed){
				return;
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
		<br/>
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
	<div class="row" id="groupSatus">
						<label class="labelCol">Group Status:</label>
						<span class="field">${ groupStatus }</span>
	
	</div>	
	<div class="row" id="groupVersion">
						<label class="labelCol">Group Version:</label>
						<span class="field">v${ groupVersion }</span>
	
	</div>	
	<div class="row" id ="groupOp">
		<form:label path="groupOperation">Group:</form:label>
		<form:checkbox path="groupOperation" id='groupChecked' value='false'/>
	</div>
	
	<c:set var="selectAllElement" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<display:table id="groupDetail" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
					requestURI="<%=WebConstants.MVC_PROVIEW_GROUP_BOOK_VERSIONS%>"
					sort="external">
		
		<display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
		<display:setProperty name="paging.banner.onepage" value=" " />
		
		<c:choose>
			<c:when test="${showSubgroup}">
				<display:column title="${selectAllElement}"  style="text-align: center">
					<form:checkbox path="groupMembers" value="${groupDetail.id}" />
				</display:column>
				<display:column title="Subgroup Name" property="subGroupName" />
				
				<c:set var="values" value="${groupDetail.titleIdList}" />
				<display:column title="Title ID" style="text-align: left">
					<table class="displayTagTable">
							<c:forEach items="${values}" var="value" varStatus="status">
								<tbody>
									<tr><td>${ value }</td></tr>
								</tbody>
							</c:forEach>
					</table>
				</display:column>
			</c:when>
			<c:otherwise>
				<display:column title="${selectAllElement}"  style="text-align: center">
					<form:checkbox path="groupMembers" value="${groupDetail.id}" />
				</display:column>
				<display:column title="Title ID" property= "titleId" />
			</c:otherwise>
		</c:choose>
		
		<display:column title="Proview Display Name" property="proviewDisplayName" />
		<display:column title="Version" property="bookVersion" />
		<display:column title="Book Status" property="bookStatus" />
	</display:table>
	
	<c:if test="${resultSize != 0}">
		<c:set var="disableButtons" value="disabled"/>
		<sec:authorize access="hasAnyRole('ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
			<c:set var="disableButtons" value=""/>
		</sec:authorize>
		<c:set var="disableRemoveButtons" value="disabled"/>
		<sec:authorize access="hasRole('ROLE_SUPERUSER')">
			<c:set var="disableRemoveButtons" value=""/>
		</sec:authorize>
		
		<div class="buttons">
			<c:choose>
				<c:when test="${pilotBookStatus != pilotInProgress}">
					<input id="promoteButton" value="Promote to Final"  ${disableButtons} type="button"  onclick="submitGroupForm('<%=GroupCmd.PROMOTE%>')"/> &nbsp;
				</c:when>
				<c:otherwise>
					Pilot book marked as 'In Progress' for notes migration. Once the note migration csv file is in place, update the Pilot Book status, and regenerate the book before Promoting. 
				</c:otherwise>
			</c:choose>
			<input id="removeButton" type="button" ${disableRemoveButtons} value="Remove" onclick="submitGroupForm('<%=GroupCmd.REMOVE%>')"/>&nbsp;
			<input id="deleteButton" type="button" ${disableRemoveButtons} value="Delete" onclick="submitGroupForm('<%=GroupCmd.DELETE%>')"/>
		</div>
	</c:if>
</form:form>
