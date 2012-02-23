<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<div class="row">
	<form:input path="authorInfo[${authorIndex}].prefix" style="width:35px;" title="prefix" />
	<form:errors path="authorInfo[${authorIndex}].prefix" cssClass="errorMessage" />
	<form:input path="authorInfo[${authorIndex}].firstName" style="width:75px;" title="first name" />
	<form:errors path="authorInfo[${authorIndex}].firstName" cssClass="errorMessage" />
	<form:input path="authorInfo[${authorIndex}].middleName" style="width:75px;" title="middle name" />
	<form:errors path="authorInfo[${authorIndex}].middleName" cssClass="errorMessage" />
	<form:input path="authorInfo[${authorIndex}].lastName"  style="width:75px;" title="last name"/>
	<form:errors path="authorInfo[${authorIndex}].lastName" cssClass="errorMessage" />
	<form:input path="authorInfo[${authorIndex}].suffix" style="width:30px;" title="suffix"/>
	<form:errors path="authorInfo[${authorIndex}].suffix" cssClass="errorMessage" />
	<input class="removeAuthor" type="button" value="Remove" />
</div>