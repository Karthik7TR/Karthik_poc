<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.core.CoreConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.stats.PublishingStatsForm"%>

<html>
<head>
<script type="text/javascript">
		
		function changePageSize(){
			$('#<%=PublishingStatsForm.FORM_NAME%>').submit();
			return true; 
		}
</script>		
</head>

	<%-- Table of publishing stats for a specific book --%>
	
	<c:set var="DATE_FORMAT" value="<%=CoreConstants.DATE_TIME_FORMAT_PATTERN %>"/>
	
	<form:form action="<%=WebConstants.MVC_STATS%>"
			   commandName="<%=PublishingStatsForm.FORM_NAME%>" name="theForm" method="post">
	
	<display:table id="<%= WebConstants.KEY_VDO %>" name="<%=WebConstants.KEY_PAGINATED_LIST%>" class="displayTagTable" cellpadding="2" 
				   requestURI="<%=WebConstants.MVC_STATS%>"
				   pagesize="${pageSize}"
				   partialList="false"
				   export="true">
				   

	  <display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:setProperty name="basic.msg.empty_list">No records found.</display:setProperty>
	  <display:setProperty name="paging.banner.onepage" value=" " />
	  <display:setProperty name="export.xml" value="false" />
	  <display:setProperty name="export.csv" value="false" />
	  <display:setProperty name="export.excel.filename" value="PublishingStats.xls" />
	  
	  <display:column title="jobInstanceId" property="jobInstanceId" />
	  <display:column title="auditId" property="auditId"  />
	  <display:column title="ebookDefId" property="ebookDefId"  />
	  <display:column title="jobSubmitterName" property="jobSubmitterName"  />
	  <display:column title="jobSubmitTimestamp" property="jobSubmitTimestamp"/>
	  <display:column title="bookVersionSubmitted" property="bookVersionSubmitted"  />
	  <display:column title="publishStartTimestamp" property="publishStartTimestamp"/>
	  <display:column title="gatherTocNodeCount" property="gatherTocNodeCount"  />
	  <display:column title="gatherTocSkippedCount" property="gatherTocSkippedCount"  />
	  <display:column title="gatherTocDocCount" property="gatherTocDocCount"  />
	  <display:column title="gatherTocRetryCount" property="gatherTocRetryCount"  />
	  <display:column title="gatherDocExpectedCount" property="gatherDocExpectedCount"  />
	  <display:column title="gatherDocRetryCount" property="gatherDocRetryCount"  />
	  <display:column title="gatherDocRetrievedCount" property="gatherDocRetrievedCount"  />
	  <display:column title="gatherMetaExpectedCount" property="gatherMetaExpectedCount"  />
	  <display:column title="gatherMetaRetryCount" property="gatherMetaRetryCount"  />
	  <display:column title="gatherMetaRetrievedCount" property="gatherMetaRetrievedCount"  />
	  <display:column title="gatherImageExpectedCount" property="gatherImageExpectedCount"  />
	  <display:column title="gatherImageRetryCount" property="gatherImageRetryCount"  />
	  <display:column title="gatherImageRetrievedCount" property="gatherImageRetrievedCount"  />
	  <display:column title="formatDocCount" property="formatDocCount"  />
	  <display:column title="assembleDocCount" property="assembleDocCount"  />
	  <display:column title="titleDupDocCount" property="titleDupDocCount"  />
	  <display:column title="publishStatus" property="publishStatus"  />
	  <display:column title="publishEndTimestamp" property="publishEndTimestamp"/>
	  <display:column title="lastUpdated" property="lastUpdated"/>
	  <display:column title="bookSize" property="bookSize"  />
	  <display:column title="largestDocSize" property="largestDocSize"  />
	  <display:column title="largestImageSize" property="largestImageSize"  />
	  <display:column title="largestPdfSize" property="largestPdfSize"  />
	  
	</display:table>
	</form:form>

</html>
