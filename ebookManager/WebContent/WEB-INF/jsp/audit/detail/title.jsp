<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<div class="pageTitle">Book Definition Audit Detail</div>
<c:if test="${bookAuditDetail != null}">	<%-- found the book --%>
	<div id="bookData">
		<div class="bookImage">
			<img alt="${book.proviewDisplayName}" src="<%= WebConstants.MVC_COVER_IMAGE %>?imageName=${ book.coverImage }">
		</div>
		<div class="titleData">
			Title ID: ${bookAuditDetail.titleId}<br/>
			ProView Display Name: ${bookAuditDetail.proviewDisplayName}<br/>
		</div>
	</div>
</c:if>