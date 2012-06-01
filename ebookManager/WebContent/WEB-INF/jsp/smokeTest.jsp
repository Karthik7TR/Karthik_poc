<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>

<html>
<head>
	<title>Smoke Test</title>
	<style type="text/css">
		.displayTagTable {
		    border: 1px solid #E0E0E0;
		    margin: 10px 0 !important;
		}
		.displayTagTable th, .displayTagTable td {
		    padding: 2px 4px !important;
		    vertical-align: top;
		}
		.tableHeader {
			text-align: left;
		}
		.Passed, .Failed {
			text-align: center;
			width: 10em;
			color:white;
		}
		
		.Passed {
			background-color: green;
		}
		
		.Failed {
			background-color: red;
		}
		
		hr {
			margin: 1em 0em;
		}
	</style>
</head>

<body>

<h1>Smoke Test Page</h1>
<table>
	<tbody>
		<tr>
			<th class="tableHeader">Environment:</th>
			<td>${ environmentName }</td>
		</tr>
		<tr>
			<th class="tableHeader">Server:</th>
			<td>${ localHost }</td>
		</tr>
		<tr>
			<th class="tableHeader">ProView:</th>
			<td>${ proviewDomain }</td>
		</tr>
		<tr>
			<th class="tableHeader">Applications:</th>
			<td>${ applications }</td>
		</tr>
		<tr>
			<th class="tableHeader">as of:</th>
			<td><fmt:formatDate value="${date}" type="both" pattern="<%= CoreConstants.DATE_TIME_MS_FORMAT_PATTERN %>" /> </td>
		</tr>
	</tbody>
</table>

<hr></hr>

<h3>Current Image Vertical, ProView Server, and Database Connection</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="currentProperties" class="displayTagTable" cellpadding="2">
	<display:column title="Server Name" property="name" />
	<display:column title="Server Address">
		<a target="_blank" href="${ vdo.address }">${ vdo.address }</a>
	</display:column>
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>


<hr></hr>

<h3>Lower Environment Database Server Ping Tests</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="lowerEnvDatabase" class="displayTagTable" cellpadding="2">
	<display:column title="Server Name" property="name" />
	<display:column title="Server Address" property="address" />
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

<hr></hr>

<h3>Prod Database Server Ping Tests</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="prodDatabase" class="displayTagTable" cellpadding="2">
	<display:column title="Server Name" property="name" />
	<display:column title="Server Address" property="address" />
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

<hr></hr>

<h3>CI Server Ping Test</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="ci" class="displayTagTable" cellpadding="2">
	<display:column title="Server Name" property="name" />
	<display:column title="Server Address" property="address" />
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

<h3>Page Retrieval Test on CI</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="ciApps" class="displayTagTable" cellpadding="2">
	<display:column title="Application Name" property="name" />
	<display:column title="Application Address">
		<a target="_blank" href="${ vdo.address }">${ vdo.address }</a>
	</display:column>
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

<hr></hr>

<h3>Test Server Ping Test</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="test" class="displayTagTable" cellpadding="2">
	<display:column title="Server Name" property="name" />
	<display:column title="Server Address" property="address" />
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

<h3>Page Retrieval Test on Test</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="testApps" class="displayTagTable" cellpadding="2">
	<display:column title="Application Name" property="name" />
	<display:column title="Application Address">
		<a target="_blank" href="${ vdo.address }">${ vdo.address }</a>
	</display:column>
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

<hr></hr>

<h3>QED Server Ping Tests</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="qa" class="displayTagTable" cellpadding="2">
	<display:column title="Server Name" property="name" />
	<display:column title="Server Address" property="address" />
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

<h3>Page Retrieval Test on QED</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="qaApps" class="displayTagTable" cellpadding="2">
	<display:column title="Application Name" property="name" />
	<display:column title="Application Address">
		<a target="_blank" href="${ vdo.address }">${ vdo.address }</a>
	</display:column>
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

<hr></hr>

<h3>Prod Server Ping Tests</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="prod" class="displayTagTable" cellpadding="2">
	<display:column title="Server Name" property="name" />
	<display:column title="Server Address" property="address" />
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>


<h3>Page Retrieval Test on Prod</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="prodApps" class="displayTagTable" cellpadding="2">
	<display:column title="Application Name" property="name" />
	<display:column title="Application Address">
		<a target="_blank" href="${ vdo.address }">${ vdo.address }</a>
	</display:column>
	<display:column title="Server Status">
		<c:set var="status" value="Failed" />
		<c:if test="${vdo.running}">
			<c:set var="status" value="Passed" />
		</c:if>
		<div class="${status}">${status}</div>
	</display:column>
</display:table>

</body>
</html>