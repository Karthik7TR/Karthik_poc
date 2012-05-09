<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.stopgenerator.StopGeneratorForm"%>

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
			"Stop All": function() {
				$('#<%= StopGeneratorForm.FORM_NAME %>').submit();
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

<form:form commandName="<%= StopGeneratorForm.FORM_NAME %>">
	Are you sure you want to stop the eBook Generator and Gatherer applications?  This will stop all running eBook jobs. 
	To stop the eBook Generator and Gatherer applications please type <b>"<%= WebConstants.CONFIRM_CODE_KILL_SWITCH %>"</b> in the
	field below and press the Stop All button.
	<div class="stopGather" style="margin:1em;">
		<form:label path="code">Code</form:label>
		<form:input path="code"/>
		<form:errors path="code" cssClass="errorMessage"/>
		<div class="info" style="font-size:.7em;">
			*This field is case sensitive
		</div>
		
	</div>
</form:form>

<div class="buttons">
	<button id="save" >Stop All</button>
	<button type="button" onclick="location.href ='<%=WebConstants.MVC_ADMIN_MAIN%>';">Cancel</button>
</div>

<div id="dialog-confirm" title="Stop All eBook Generators?" style="display:hidden;" >
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>eBook Generators will be stopped. Are you sure?</p>
</div>

