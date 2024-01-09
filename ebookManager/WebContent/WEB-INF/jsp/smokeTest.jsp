<!DOCTYPE html>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.smoketest.service.SmokeTestServiceImpl"%>

<html>
<head>
	<title>Smoke Test Page</title>
	<style type="text/css">
		.displayTagTable {
		    border: 1px solid #E0E0E0;
		    margin: 10px 0 !important;
		}
		.displayTagTable th, .displayTagTable td {
		    padding: 2px 4px !important;
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

		.noBull {
			list-style-type: none;
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
			<th class="tableHeader">ProView Host:</th>
			<td>${ proviewHost }</td>
		</tr>
		<tr>
			<th class="tableHeader">Novus Environment:</th>
			<td>${ novusEnvironment }</td>
		</tr>
		<tr>
			<th class="tableHeader">as of:</th>
			<td><fmt:formatDate value="${date}" type="both" pattern="<%= CoreConstants.DATE_TIME_MS_FORMAT_PATTERN %>" /> </td>
		</tr>
	</tbody>
</table>

<hr></hr>

<h3>Current application version</h3>
<table class="displayTagTable">
	<thead>
	<tr>
		<th>Environment</th>
		<th>Application Version</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach var="application" items="${applications}">
		<tr>
			<td style="text-align: center">${application.key}</td>
			<td>
				<ul class="noBull">
					<c:forEach var="version" items="${application.value}">
						<li>${version}</li>
					</c:forEach>
				</ul>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>

<h3>Current Image Vertical, ProView Server, and Database Connection</h3>
<display:table id="<%= WebConstants.KEY_VDO %>" name="currentProperties" class="displayTagTable" cellpadding="2">
	<display:column title="Server Name" property="name" />
	<display:column title="More Information">
		<c:choose>
			<c:when test="${fn:containsIgnoreCase(vdo.address, 'http')}">
				<a target="_blank" href="${ vdo.address }">${ vdo.address }</a>
			</c:when>
			<c:otherwise>
				${ vdo.address }
			</c:otherwise>
		</c:choose>
		
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

<hr>

</hr>

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