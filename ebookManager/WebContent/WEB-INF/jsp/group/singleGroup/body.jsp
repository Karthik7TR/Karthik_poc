<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupListFilterForm.GroupCmd"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupListFilterForm"%>
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
		$(this).parents('#<%= WebConstants.KEY_GROUP %>').find(':checkbox').attr('checked', this.checked);
	});
});

function submitGroupForm(command) {
	
		$("#groupCmd").val(command);  // Set the form hidden field value for the operation discriminator
		
		$("#multiSelectForm").submit();	// POST the HTML form with the selected start/restart jobs
	
}

</script>
<form:form id="multiSelectForm" action="<%=WebConstants.MVC_GROUP_OPERATION%>"
		   commandName="<%=GroupListFilterForm.FORM_NAME%>" name="theForm" method="post">
		   <form:hidden path="groupCmd"/>
		   <form:hidden path="groupName" value="${groupName}"/>
		   <form:hidden path="groupStatus" value="${groupStatus}"/>
		   <form:hidden path="bookDefinitionId" value="${bookDefinitionId}"/>
		   <form:hidden path="proviewGroupID" value="${proviewGroupID}"/>
		   <form:hidden path="groupVersion" value="${groupVersion}"/>
		<!--     
		<spring:hasBindErrors name="<%=GroupListFilterForm.FORM_NAME%>">
		<div class="errorBox">
	      <b><spring:message code="please.fix.errors"/>:</b><br/>
	      <form:errors path="*">
	      	<ul>
			<c:forEach items="${errors.allErrors}" var="error">
				<li style="color: black">${error}</li>
			</c:forEach>
	      	</ul>
		  </form:errors>
		  <br/>
	    </div>
    </spring:hasBindErrors>
    -->
    
     <spring:hasBindErrors name="<%=GroupListFilterForm.FORM_NAME%>">
		 <b><spring:message code="please.fix.errors"/>:</b><br/>
			<font color="red">
		        <c:forEach var="error" items="${errors.allErrors}">
       			 <b><spring:message message="${error}" /></b>
        		 <br/>
	        	</c:forEach>
	        </font>
	 </spring:hasBindErrors>
    
	<div class="row" id ="groupName">
						<label class="labelCol" >Group Name:</label>
						<span class="field">${ groupName }</span>
	
	</div>
	<div class="row" id ="groupStatus">
						<label class="labelCol">Group Status:</label>
						<span class="field">${ groupStatus }</span>
	
	</div>
	<c:set var="selectAllElement" value="<input type='checkbox' id='selectAll' value='false' />"/>
	<display:table id="group" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_GROUP_BOOK_ALL_VERSIONS%>"
				   sort="external">
	  
	  <display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:column title="${selectAllElement}"  style="text-align: center">
  		<form:checkbox path="groupIds" value="${group.id}" />
  	  </display:column>
  	  <display:column title="Subgroup Name" property="subGroupName" />
  	  <c:set var="values" value="${group.splitTitles}" />
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
	  <display:column title="Version" property="version" />
	  <display:column title="Book Status" property="status" />
	</display:table>
	
	<c:if test="${resultSize != 0}">
	  <c:set var="disableButtons" value="disabled"/>
	  <sec:authorize access="hasAnyRole('ROLE_PUBLISHER_PLUS,ROLE_SUPERUSER')">
	  	<c:set var="disableButtons" value=""/>
	  </sec:authorize>
	  
	  <div class="buttons">
		  <input type="button" ${disableButtons} value="Promote to Final"  onclick="submitGroupForm('<%=GroupCmd.PROMOTE%>')"/> &nbsp;
		  <input type="button" ${disableButtons} value="Remove" onclick="submitGroupForm('<%=GroupCmd.REMOVE%>')"/>&nbsp;
		  <input type="button" ${disableButtons} value="Delete" onclick="submitGroupForm('<%=GroupCmd.DELETE%>')"/>
	  </div>
	</c:if>
</form:form>



