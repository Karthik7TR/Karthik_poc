<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.userprofile.UserProfileForm"%>


<form:form commandName="<%= UserProfileForm.FORM_NAME %>">
	<table>
		<tr>
			<td>
				<form:label path="userId">User ID</form:label>
			</td>
			<td>
				<form:input path="userId" required="required" pattern="\S(.*\S)?" title="Remove Whitespace - Begining/End"/>
			</td>
			<td>
				<form:errors path="userId" cssClass="errorMessage"/>
			</td>
		</tr>
		<tr>
			<td>
				<form:label path="firstName">First Name</form:label>
			</td>
			<td>
				<form:input path="firstName"  maxlength="4000" required="required" pattern="\S(.*\S)?" title="Remove Whitespace - Begining/End"/>
			</td>
			<td>
				<form:errors path="firstName" cssClass="errorMessage"/>
			</td>
		</tr>
		<tr>
			<td>
				<form:label path="lastName">Last Name</form:label>
			</td>
			<td>
				<form:input path="lastName"  maxlength="4000"  required="required" pattern="\S(.*\S)?" title="Remove Whitespace - Begining/End"/>
			</td>
			<td>
				<form:errors path="lastName" cssClass="errorMessage"/>
			</td>
		</tr>
	</table>

	<div class="buttons">
		<form:button id="save">Save</form:button>
		<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_USER_PROFILE_VIEW%>';">Cancel</button>
	</div>
</form:form>
