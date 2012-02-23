<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<div class="bookDefinitionCRUD">
<script type="text/javascript">
var incrementVar = 1;
$(function(){
	$(".rdelete").live("click", function () {
		var srow = $(this).parent();
		srow.css("background-color", "#F0F0F0");
		srow.fadeOut(500, function () { srow.remove(); });
	});
});

function AddNew() {
	var appendTxt = "<div class='row'>";
	appendTxt = appendTxt + "<input class=\"prefix\" id=\"authorInfo" + incrementVar + ".prefix\" name=\"authorInfo[" + incrementVar + "].prefix\" type=\"text\" title=\"prefix\"/>";
	appendTxt = appendTxt + "<input class=\"firstName\" id=\"authorInfo" + incrementVar + ".firstName\" name=\"authorInfo[" + incrementVar + "].firstName\" type=\"text\" title=\"first name\"/>";
	appendTxt = appendTxt + "<input class=\"middleName\" id=\"authorInfo" + incrementVar + ".middleName\" name=\"authorInfo[" + incrementVar + "].middleName\" type=\"text\" title=\"middle name\"/>";
	appendTxt = appendTxt + "<input class=\"lastName\" id=\"authorInfo" + incrementVar + ".lastName\" name=\"authorInfo[" + incrementVar + "].lastName\" type=\"text\" title=\"last name\"/>";
	appendTxt = appendTxt + "<input class=\"suffix\" id=\"authorInfo" + incrementVar + ".suffix\" name=\"authorInfo[" + incrementVar + "].suffix\" type=\"text\" title=\"suffix\"/>";
	appendTxt = appendTxt + "<input type=\"button\" value=\"Delete\" class=\"rdelete\" />";
	appendTxt = appendTxt + "</div>";
	$("#addHere").before(appendTxt);
	incrementVar = incrementVar + 1;
	
	textboxHint("authorName");
} 

</script>
	<form:form commandName="<%= EditBookDefinitionForm.FORM_NAME %>"
		action="<%=WebConstants.MVC_BOOK_DEFINITION_CREATE%>" >
		<jsp:include page="../common/crudForm.jsp" />
		<div id="authorName">
			<form:label path="authorInfo" class="labelCol">Author Information</form:label>
			<c:forEach items="${editBookDefinitionForm.authorInfo}" var="author" varStatus="aStatus">
				<div class="row">
					<form:input path="authorInfo[${aStatus.index}].prefix" title="prefix" class="prefix"  />
					<form:input path="authorInfo[${aStatus.index}].firstName"  title="first name" class="firstName" />
					<form:input path="authorInfo[${aStatus.index}].middleName"  title="middle name" class="middleName" />
					<form:input path="authorInfo[${aStatus.index}].lastName"   title="last name" class="lastName" />
					<form:input path="authorInfo[${aStatus.index}].suffix"  title="suffix" class="suffix" />
					<input type="button" value="Delete" class="rdelete" />
				</div>
			</c:forEach>
			<div id="addHere"></div>
			<input type="button" onclick="AddNew();" id="addAuthor" value="add" />
		</div>
		<div class="buttons">
			<form:button>Validate</form:button>
			<form:button>Save</form:button>
			<a href="<%= WebConstants.MVC_BOOK_LIBRARY_LIST %>">Cancel</a>
		</div>
		
	</form:form>
</div>

