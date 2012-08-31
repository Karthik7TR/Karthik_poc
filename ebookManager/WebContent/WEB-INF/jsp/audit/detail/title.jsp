<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="pageTitle">Book Definition Audit Detail</div>
<c:if test="${bookAuditDetail != null}">	<%-- found the book --%>
	<div id="bookData">
		<div class="bookImage">
			<img alt="${fn:escapeXml(book.proviewDisplayName)}" src="<%= WebConstants.MVC_COVER_IMAGE %>?imageName=${ book.coverImage }">
		</div>
		<div class="titleData">
			<div><label>Title ID:</label> ${bookAuditDetail.titleId}</div>
			<div><label>ProView Display Name:</label> ${fn:escapeXml(bookAuditDetail.proviewDisplayName)}</div>
		</div>
	</div>
</c:if>
