<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.userpreferences.UserPreferencesForm" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookaudit.BookAuditFilterForm"%>
<%@page import="org.springframework.batch.core.BatchStatus"%>

<script type="text/javascript">
		// Declare Global Variables
		const EMAIL_ID_PREFIX = "emails";
		var emailIndex = ${numberOfEmails};
		
        var onClickToDeleteButton = function () {
            var srow = $(this).parent();
           // var input = srow.find("input")[0];
            //console.log(input);
            //input.remove();
            srow.remove();
            
         };
		// Add another author row
		var addEmailRow = function() {
			var expandingBox = $("<div>").addClass("expandingBox");
			var id = EMAIL_ID_PREFIX + emailIndex;
			var name = "emails[" + emailIndex + "]";
			
			// Add author name input boxes
			expandingBox.append($("<input>").attr("id", id).attr("name", name).attr("type", "text").attr("class", "email_class"));
			
			// Add delete button
			expandingBox.append($("<input>").addClass("rdelete").attr("id","deleteButton" + emailIndex).attr("title","Delete Email").attr("type", "button").val("Delete").on("click", onClickToDeleteButton));
		
			$("#addEmailHere").before(expandingBox);
			emailIndex = emailIndex + 1;
		};
		
		$(document).ready(function() {
			<%-- Setup Button Click handlers  --%>
			$('#addEmail').click(function () {
				addEmailRow();
			});
			
			$("#confirm").click(function () {
				var result = true;
				var emailIds = $(".email_class");
				for(index=0; index<emailIds.length; index++) {
					var emailIdValue = emailIds[index].value;
					var emailIndexId = emailIds[index].id;
					var indexId =  emailIndexId.substr(EMAIL_ID_PREFIX.length);
					var deleteButton = $("#deleteButton" + indexId);
					
					deleteButton.parent().find(".errorDiv").remove();
					
					if(emailIdValue == undefined || $.trim(emailIdValue).length == 0) {
						var errorDiv = $("<div>").addClass("errorDiv");
						errorDiv.append($("<span>").attr("id","emails"+indexId+"2.errors").attr("class", "errorMessage").text("Invalid Email"));
						deleteButton.after(errorDiv);
						result = false;
						//break;
					}
				}
				
				return result;
				
			});
			
			// delete confirmation box
			$(".rdelete").on("click", onClickToDeleteButton);
		});
		
		
		
		
</script>

<form:form id="preferencesForm" commandName="<%= UserPreferencesForm.FORM_NAME %>" action="<%=WebConstants.MVC_USER_PREFERENCES%>" >
	<div class="filterBox">
		<label class="labelCol">Library List Filters</label>
		<div class="row">
			<label>ProView Display Name</label>
			<form:input path="libraryFilterProviewName"/>
			<div class="errorDiv">
				<form:errors path="libraryFilterProviewName" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<label>Title ID</label>
			<form:input path="libraryFilterTitleId"/>
			<div class="errorDiv">
				<form:errors path="libraryFilterTitleId" cssClass="errorMessage" />
			</div>
		</div>
	</div>
	
	<div class="filterBox">
		<label class="labelCol">Audit List Filters</label>
		<div class="row">
			<label>ProView Display Name</label>
			<form:input path="auditFilterProviewName"/>
			<div class="errorDiv">
				<form:errors path="auditFilterProviewName" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<label>Title ID</label>
			<form:input path="auditFilterTitleId"/>
			<div class="errorDiv">
				<form:errors path="auditFilterTitleId" cssClass="errorMessage" />
			</div>
		</div>
	</div>
	
	<div class="filterBox">
		<label class="labelCol">Job Summary Filters</label>
		<div class="row">
			<label>ProView Display Name</label>
			<form:input path="jobSummaryFilterProviewName"/>
			<div class="errorDiv">
				<form:errors path="jobSummaryFilterProviewName" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<label>Title ID</label>
			<form:input path="jobSummaryFilterTitleId"/>
			<div class="errorDiv">
				<form:errors path="jobSummaryFilterTitleId" cssClass="errorMessage" />
			</div>
		</div>
	</div>
	
	<div class="filterBox">
		<label class="labelCol">Group List Filters</label>
		<div class="row">
			<label>Group Name</label>
			<form:input path="groupFilterName"/>
			<div class="errorDiv">
				<form:errors path="groupFilterName" cssClass="errorMessage" />
			</div>
		</div>
		<div class="row">
			<label>Group ID</label>
			<form:input path="groupFilterId"/>
			<div class="errorDiv">
				<form:errors path="groupFilterId" cssClass="errorMessage" />
			</div>
		</div>
	</div>

	<div class="row">
		<label class="labelCol">Email</label>
		<input type="button" id="addEmail" value="add" />
		<div class="errorDiv">
			<form:errors path="emails" cssClass="errorMessage" />
		</div>
		<c:forEach items="${preferencesForm.emails}" var="email" varStatus="eStatus">
			<div class="expandingBox">
				<form:input path="emails[${eStatus.index}]" cssClass="email_class"/>
				<input type="button" id="deleteButton${eStatus.index}" value="Delete" class="rdelete" title="Delete Email" />
				<div class="errorDiv">
					<form:errors path="emails[${eStatus.index}]" cssClass="errorMessage" />
				</div>
			</div>
		</c:forEach>
		<div id="addEmailHere"></div>
	</div>
	
	<div class="row">	
		<label class="labelCol">Start Page</label>
		<div class="errorDiv">
			<form:errors path="startPage" cssClass="errorMessage" />
		</div>
		<form:select path="startPage">
			<%-- Check if user has role to access Admin --%>
			<c:set var="showAdmin" value="false"/>
			<sec:authorize access="hasAnyRole('ROLE_SUPPORT,ROLE_SUPERUSER')">
				<c:set var="showAdmin" value="true"/>
			</sec:authorize>
			<c:if test="${showAdmin}">
				<form:option label="<%=UserPreferencesForm.HomepageProperty.ADMINISTRATION.toString()%>" value="<%=UserPreferencesForm.HomepageProperty.ADMINISTRATION.toString() %>"/>
			</c:if>
			<form:option label="<%=UserPreferencesForm.HomepageProperty.AUDIT.toString()%>" value="<%=UserPreferencesForm.HomepageProperty.AUDIT.toString() %>"/>
			<form:option label="<%=UserPreferencesForm.HomepageProperty.GROUP_LIST.toString()%>" value="<%=UserPreferencesForm.HomepageProperty.GROUP_LIST.toString() %>"/>
			<form:option label="<%=UserPreferencesForm.HomepageProperty.JOBS.toString()%>" value="<%=UserPreferencesForm.HomepageProperty.JOBS.toString() %>"/>
			<form:option label="<%=UserPreferencesForm.HomepageProperty.LIBRARY.toString()%>" value="<%=UserPreferencesForm.HomepageProperty.LIBRARY.toString() %>"/>
			<form:option label="<%=UserPreferencesForm.HomepageProperty.PROVIEW_LIST.toString()%>" value="<%=UserPreferencesForm.HomepageProperty.PROVIEW_LIST.toString() %>"/>
			<form:option label="<%=UserPreferencesForm.HomepageProperty.QUEUED.toString()%>" value="<%=UserPreferencesForm.HomepageProperty.QUEUED.toString() %>"/>
		</form:select>
	</div>
	<div class="buttons">
		<form:button type="submit" id="confirm">Save</form:button>
	</div>
</form:form>