<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit.EditGroupDefinitionForm"%>

<script type="text/javascript" src="js/create-group.js"></script>
<script type="text/javascript" src="js/common-form.js"></script>
<script type="text/javascript" src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/additional-methods.min.js"></script>

<form:hidden path="bookDefinitionId" />
<form:hidden path="fullyQualifiedTitleId" />
<form:hidden path="hasSplitTitles" />
<form:hidden path="combined"/>

<%-- Error Messages area from Proview --%>
<c:if test="${warningMessage != null}">
	<div class="infoMessageWarning">
		${warningMessage}
	</div>
	<br/>
</c:if>

<div class="centerSection">
	<div class="leftDefinitionForm">
		<div class="row">
			<label >ProView Status:</label>
			<span>${ groupStatusInProview }</span>
		</div>
		<div class="row">
			<form:label path="groupName">Group Name:</form:label>
			<form:input path="groupName"/>
			<div class="errorDiv">
				<form:errors path="groupName" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<form:label path="groupId">Group ID:</form:label>
			<form:input path="groupId" disabled="true"/>
			<form:hidden path="groupId" />
			<div class="errorDiv">
				<form:errors path="groupId" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<form:label path="groupType">Group Type:</form:label>
			<form:input path="groupType" disabled="true"/>
			<form:hidden path="groupType" />
			<div class="errorDiv">
				<form:errors path="groupType" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<form:label path="includeSubgroup">Include Subgroup(s):</form:label>
			<form:radiobutton path="includeSubgroup" value="true" />True
			<form:radiobutton path="includeSubgroup" value="false" />False
			<div class="errorDiv">
				<form:errors path="includeSubgroup" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<label>ProView Version Type:</label>
			<span>${ overwriteAllowed }</span>
			<div class="errorDiv">
				<form:errors path="versionType" cssClass="errorMessage" />
			</div>
		</div>
		<div id="showPilotBooks">
			<div class="row">
				<form:label path="includePilotBook">Include Pilot Book(s):</form:label>
				<form:radiobutton path="includePilotBook" value="true" />True
				<form:radiobutton path="includePilotBook" value="false" />False
				<div class="errorDiv">
					<form:errors path="includePilotBook" cssClass="errorMessage" />
				</div>
			</div>
			<div id="displayPilotBooks">
				<div><br>Included Pilot Books:</div>
				<div class="errorDiv">
					<form:errors path="pilotBooks" cssClass="errorMessage" />
				</div>
				<ol id="pilotTitles">
					<c:forEach items="${editGroupDefinitionForm.pilotBooks.titles}" var="pilotBook" varStatus="pilotStatus">
						<li class="ui-state-default">
							<div class="bookInfo">
								<div>ProView Name: ${pilotBook.proviewName}</div>
								<div>Title ID: ${pilotBook.titleId}</div>
								<div>Major Version: ${pilotBook.version}</div>
								<form:hidden attr="titleId" path="pilotBooks.titles[${pilotStatus.index}].titleId" />
								<form:hidden attr="proviewName" path="pilotBooks.titles[${pilotStatus.index}].proviewName" />
								<form:hidden attr="version" path="pilotBooks.titles[${pilotStatus.index}].version" />
							</div>
						</li>
					</c:forEach>
				</ol>
			</div>
		</div>
		<div id="notgrouped">
			<div>Unassigned Versions</div>
			<div class="errorDiv">
				<form:errors path="notGrouped" cssClass="errorMessage" />
			</div>
			<ul id="titles" class="drop">
				<c:forEach items="${editGroupDefinitionForm.notGrouped.titles}" var="notGroupedTitle" varStatus ="notGroupedStatusTitleStatus">
					<li class="ui-state-default">
						<div class="bookInfo">
							<div>Base ProView Name: ${notGroupedTitle.proviewName}</div>
							<div>Title ID: ${notGroupedTitle.titleId}</div>
							<div>Major Version: ${notGroupedTitle.version}</div>
							<div>Number of parts: ${notGroupedTitle.numberOfParts}</div>
							<form:hidden attr="titleId" path="notGrouped.titles[${notGroupedStatusTitleStatus.index}].titleId" />
							<form:hidden attr="proviewName" path="notGrouped.titles[${notGroupedStatusTitleStatus.index}].proviewName" />
							<form:hidden attr="version" path="notGrouped.titles[${notGroupedStatusTitleStatus.index}].version" />
							<form:hidden attr="numberOfParts" path="notGrouped.titles[${notGroupedStatusTitleStatus.index}].numberOfParts" />
						</div>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
	<div class="rightDefinitionForm">
		<button id="addSubgroup" type="button">Add Subgroup</button>
		<div id="groups" >
			<c:forEach items="${editGroupDefinitionForm.subgroups}" var="subgroup" varStatus ="status">
				<div class="group">
					<button type="button" class="removeSubgroup">Remove Subgroup</button>
					<div class="row">
						<form:label path="subgroups[${status.index}].heading">Subgroup ${status.index + 1} heading:</form:label>
						<form:input class="subheading" path="subgroups[${status.index}].heading" />
						<div class="errorDiv">
							<form:errors path="subgroups[${status.index}].heading" cssClass="errorMessage" />
						</div>
					</div>
					<ul class="drop">
						<c:forEach items="${subgroup.titles}" var="title" varStatus ="titleStatus">
							<li class="ui-state-default">
								<div class="bookInfo">
									<div>Base ProView Name: ${title.proviewName}</div>
									<div>Title ID: ${title.titleId}</div>
									<div>Major Version: ${title.version}</div>
									<div>Number of parts: ${title.numberOfParts}</div>
									<form:hidden attr="titleId" path="subgroups[${status.index}].titles[${titleStatus.index}].titleId" />
									<form:hidden attr="proviewName" path="subgroups[${status.index}].titles[${titleStatus.index}].proviewName" />
									<form:hidden attr="version" path="subgroups[${status.index}].titles[${titleStatus.index}].version" />
									<form:hidden attr="numberOfParts" path="subgroups[${status.index}].titles[${titleStatus.index}].numberOfParts" />
								</div>
							</li>
						</c:forEach>
					</ul>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<div id="modal"> 
    <div id="dialog" class="window" style="display:none;">
        <div class="modelTitle">Comments</div>
        <form:textarea path="comment"/>
        <form:errors path="comment" cssClass="errorMessage" />
        <div class="modalButtons">
        	<form:button class="save">Save</form:button>
        	<form:button class="closeModal">Cancel</form:button>
        </div>
    </div>
    <div id="mask"></div>
</div>

<div id="delete-confirm" title="Delete?" style="display:none;" >
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:4px 7px 70px 0;"></span>Are you sure you want to delete subgroup?</p>
</div>