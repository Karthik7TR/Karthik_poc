<!--
	Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
	Proprietary and Confidential information of TRGR. Disclosure, Use or
	Reproduction without the written authorization of TRGR is prohibited
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el" %>


	<%-- Table of publishing stats for a specific book --%>
<table>
	<tr>			
		<td><h5>Job Instance ID:</h5></td> 
		<td>${publishingStats.jobInstanceId}</td>
	</tr>	
	
	<tr>
		<td><h5>Audit ID:</h5></td> 
		<td>${publishingStats.auditId}</td>
	</tr>
	
	<tr>
		<td><h5>eBook Definition ID:</h5></td>
		<td>${publishingStats.ebookDefId}</td>
	</tr>
		
	<tr>
		<td><h5>Book Version:</h5></td>
		<td>${publishingStats.bookVersionSubmitted}</td>
	</tr>	
	<tr>
		<td><h5>Job Host Name:</h5></td> 
		<td>${publishingStats.jobHostName}</td>
	</tr>
		
	<tr>
		<td><h5>Job Submitter Name:</h5></td>
		<td>${publishingStats.jobSubmitterName}</td>
	</tr>
		
	<tr>
		<td><h5>Job Submit Timestamp:</h5></td>
		<td> ${publishingStats.jobSubmitTimestamp}</td>
	</tr>
		
	<tr>
		<td><h5>Publish Start Timestamp:</h5></td> 
		<td>${publishingStats.publishStartTimestamp}</td>
	</tr>	
	
	<tr>
		<td><h5>Gather Toc Node Count:</h5></td> 
		<td>${publishingStats.gatherTocNodeCount}</td>
	</tr>
	
	<tr>
		<td><h5>Gather Toc Skipped Count:</h5></td> 
		<td>${publishingStats.gatherTocSkippedCount}</td>
	</tr>
	
	<tr>
		<td><h5>Gather Toc Doc Count:</h5></td> 
		<td>${publishingStats.gatherTocDocCount}</td>
	</tr>	
		
	<tr>
		<td><h5>Gather Toc Retry Count:</h5></td> 
		<td>${publishingStats.gatherTocRetryCount}</td>
	</tr>	
	
	<tr>
		<td><h5>Gather Doc Expected Count:</h5></td> 
		<td>${publishingStats.gatherDocExpectedCount}</td>
	</tr>	
	
	<tr>
		<td><h5>Gather Doc Retry Count:</h5></td> 
		<td>${publishingStats.gatherDocRetryCount}</td>
	</tr>
	
	<tr>
		<td><h5>Gather Doc Retrieved Count:</h5></td> 
		<td>${publishingStats.gatherDocRetrievedCount}</td>
	</tr>
	
	<tr>
		<td><h5>Gather Meta Expected Count:</h5></td> 
		<td>${publishingStats.gatherMetaExpectedCount}</td>
	</tr>
	
	<tr>
		<td><h5>Gather Meta Retry Count:</h5></td> 
		<td>${publishingStats.gatherMetaRetryCount}</td>
	</tr>
	
	<tr>
		<td><h5>Gather Meta Retrieved Count:</h5></td> 
		<td>${publishingStats.gatherMetaRetrievedCount}</td>
	</tr>
	
		<tr>
		<td><h5>Gather Image Expected Count:</h5></td> 
		<td>${publishingStats.gatherImageExpectedCount}</td>
	</tr>
	
	<tr>
		<td><h5>Gather Image Retry Count:</h5></td> 
		<td>${publishingStats.gatherImageRetryCount}</td>
	</tr>
	
	<tr>
		<td><h5>Gather Image Retrieved Count:</h5></td> 
		<td>${publishingStats.gatherImageRetrievedCount}</td>
	</tr>
	
	<tr>
		<td><h5>Format Doc Count:</h5></td> 
		<td>${publishingStats.formatDocCount}</td>
	</tr>
	
	<tr>
		<td><h5>Assemble Doc Count:</h5></td> 
		<td>${publishingStats.assembleDocCount}</td>
	</tr>
	
	<tr>
		<td><h5>Title Doc Count:</h5></td> 
		<td>${publishingStats.titleDocCount}</td>
	</tr>
	
	<tr>
		<td><h5>Title Dup Doc Count:</h5></td> 
		<td>${publishingStats.titleDupDocCount}</td>
	</tr>
	
	<tr>
		<td><h5>Publishing Status:</h5></td> 
		<td>${publishingStats.publishStatus}</td>
	</tr>
	
	<tr>
		<td><h5>Publish End Time Stamp</h5></td> 
		<td>${publishingStats.publishEndTimestamp}</td>
	</tr>
	
	<tr>
		<td><h5>Last Updated</h5></td> 
		<td>${publishingStats.lastUpdated}</td>
	</tr>
	
		
</table>		
	
	 

