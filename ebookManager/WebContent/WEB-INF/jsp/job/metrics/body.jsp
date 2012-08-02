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
		<td><b>Job Instance ID:</b></td> 
		<td>${publishingStats.jobInstanceId}</td>
	</tr>	
	
	<tr>
		<td><b>Book Definition ID:</b></td>
		<td>${publishingStats.ebookDefId}</td>
	</tr>
		
	<tr>
		<td><b>Book Version:</b></td>
		<td>${publishingStats.bookVersionSubmitted}</td>
	</tr>	
	<tr>
		<td><b>Job Host Name:</b></td> 
		<td>${publishingStats.jobHostName}</td>
	</tr>
		
	<tr>
		<td><b>Job Submitter Name:</b></td>
		<td>${publishingStats.jobSubmitterName}</td>
	</tr>
		
	<tr>
		<td><b>Job Submit Timestamp:</b></td>
		<td> ${publishingStats.jobSubmitTimestamp}</td>
	</tr>
		
	<tr>
		<td><b>Publish Start Timestamp:</b></td> 
		<td>${publishingStats.publishStartTimestamp}</td>
	</tr>	
	
	<tr>
		<td><b>Gather Toc Node Count:</b></td> 
		<td>${publishingStats.gatherTocNodeCount}</td>
	</tr>
	
	<tr>
		<td><b>Gather Toc Skipped Count:</b></td> 
		<td>${publishingStats.gatherTocSkippedCount}</td>
	</tr>
	
	<tr>
		<td><b>Gather Toc Doc Count:</b></td> 
		<td>${publishingStats.gatherTocDocCount}</td>
	</tr>	
		
	<tr>
		<td><b>Gather Toc Retry Count:</b></td> 
		<td>${publishingStats.gatherTocRetryCount}</td>
	</tr>	
	
	<tr>
		<td><b>Gather Doc Expected Count:</b></td> 
		<td>${publishingStats.gatherDocExpectedCount}</td>
	</tr>	
	
	<tr>
		<td><b>Gather Doc Retry Count:</b></td> 
		<td>${publishingStats.gatherDocRetryCount}</td>
	</tr>
	
	<tr>
		<td><b>Gather Doc Retrieved Count:</b></td> 
		<td>${publishingStats.gatherDocRetrievedCount}</td>
	</tr>
	
	<tr>
		<td><b>Gather Meta Expected Count:</b></td> 
		<td>${publishingStats.gatherMetaExpectedCount}</td>
	</tr>
	
	<tr>
		<td><b>Gather Meta Retry Count:</b></td> 
		<td>${publishingStats.gatherMetaRetryCount}</td>
	</tr>
	
	<tr>
		<td><b>Gather Meta Retrieved Count:</b></td> 
		<td>${publishingStats.gatherMetaRetrievedCount}</td>
	</tr>
	
		<tr>
		<td><b>Gather Image Expected Count:</b></td> 
		<td>${publishingStats.gatherImageExpectedCount}</td>
	</tr>
	
	<tr>
		<td><b>Gather Image Retry Count:</b></td> 
		<td>${publishingStats.gatherImageRetryCount}</td>
	</tr>
	
	<tr>
		<td><b>Gather Image Retrieved Count:</b></td> 
		<td>${publishingStats.gatherImageRetrievedCount}</td>
	</tr>
	
	<tr>
		<td><b>Format Doc Count:</b></td> 
		<td>${publishingStats.formatDocCount}</td>
	</tr>
	
	<tr>
		<td><b>Assemble Doc Count:</b></td> 
		<td>${publishingStats.assembleDocCount}</td>
	</tr>
	
	<tr>
		<td><b>Title Doc Count:</b></td> 
		<td>${publishingStats.titleDocCount}</td>
	</tr>
	
	<tr>
		<td><b>Title Dup Doc Count:</b></td> 
		<td>${publishingStats.titleDupDocCount}</td>
	</tr>
	
	<tr>
		<td><b>Publishing Status:</b></td> 
		<td>${publishingStats.publishStatus}</td>
	</tr>
	
	<tr>
		<td><b>Publish End Time Stamp</b></td> 
		<td>${publishingStats.publishEndTimestamp}</td>
	</tr>
	
	<tr>
		<td><b>Last Updated</b></td> 
		<td>${publishingStats.lastUpdated}</td>
	</tr>
	
	<tr>
		<td><b>Book Size</b></td> 
		<td>
			<c:if test="${ publishingStats.bookSize != null }">
				${publishingStats.bookSize} (${publishingStats.bookSizeHumanReadable})
			</c:if>
		</td>
	</tr>
	
	<tr>
		<td><b>Largest Document Size</b></td> 
		<td>
			<c:if test="${ publishingStats.largestDocSize != null }">
				${publishingStats.largestDocSize} (${publishingStats.largestDocSizeHumanReadable})
			</c:if>
		</td>
	</tr>
	
	<tr>
		<td><b>Largest Image Size</b></td> 
		<td>
			<c:if test="${ publishingStats.largestImageSize != null }">
				${publishingStats.largestImageSize} (${publishingStats.largestImageSizeHumanReadable})
			</c:if>
		</td>
	</tr>
	
	<tr>
		<td><b>Largest PDF Size</b></td> 
		<td>
			<c:if test="${ publishingStats.largestPdfSize != null }">
				${publishingStats.largestPdfSize} (${publishingStats.largestPdfSizeHumanReadable})
			</c:if>
		</td>
	</tr>
</table>		
	
	 

