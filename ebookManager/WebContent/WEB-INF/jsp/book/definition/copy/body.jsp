<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<div class="bookDefinitionCRUD">

	<form:form commandName="<%= EditBookDefinitionForm.FORM_NAME %>"
		action="<%=WebConstants.MVC_BOOK_DEFINITION_COPY%>" >
		<jsp:include page="../common/crudForm.jsp" />
		<div class="buttons">
			<div class="row">
				<form:label path="isComplete" class="labelCol">Book Definition Status</form:label>
				<form:radiobutton path="isComplete" value="true" />Complete
				<form:radiobutton path="isComplete" value="false" />Incomplete
				<div class="errorDiv">
					<form:errors path="isComplete" cssClass="errorMessage" />
				</div>
			</div>
			<form:button id="validate">Validate</form:button>
			<form:button id="confirm">Save</form:button>
			<a href="<%= WebConstants.MVC_BOOK_LIBRARY_LIST %>">Cancel</a>
		</div>
		
	</form:form>
</div>

