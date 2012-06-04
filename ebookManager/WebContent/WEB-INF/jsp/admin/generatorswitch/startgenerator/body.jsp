<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<script type="text/javascript">
$(document).ready(function() {
	$( "#dialog-confirm" ).dialog({
		autoOpen: false,
		resizable: false,
		height:260,
		width:500,
		modal: true,
		draggable:false,
		buttons: {
			"Start All": function() {
				$('#form').submit();
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		}
	});
	
	$('#save').click(function () {
		$( "#dialog-confirm" ).dialog( "open" );
	});
});
</script>

<%-- Informational messages - used to report status. --%>
<c:if test="${fn:length(infoMessages) > 0}">
	
	<ul>
		<c:forEach items="${infoMessages}" var="message">
			<c:if test="${message.type == 'INFO'}">
				<c:set var="class" value="infoMessageWarning"/>
			</c:if>
			<c:if test="${message.type == 'ERROR'}">
				<c:set var="class" value="infoMessageError"/>
			</c:if>
			<li class="${class}">${message.text}</li>
		</c:forEach>
		<li class="info">Note: A successful start of application does not return any status message.</li>
	</ul>
</c:if>	   

<form:form id="form">
	Are you sure you want to start the Book Generator and Gatherer applications?  This will start the generation process.
</form:form>

<div class="buttons">
	<button id="save" >Start All</button>
	<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_MAIN%>';">Cancel</button>
</div>

<div id="dialog-confirm" title="Start All Book Generators?" style="display:hidden;" >
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Book Generators will be started. Are you sure?</p>
</div>

