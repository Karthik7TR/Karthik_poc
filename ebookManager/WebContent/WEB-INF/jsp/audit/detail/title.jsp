<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="pageTitle">Book Definition Audit Detail</div>
<c:if test="${bookAuditDetail != null}">	<%-- found the book --%>
	<div id="bookData">
		<div class="bookImage">
			<img alt="${bookAuditDetail.proviewDisplayName}" src="theme/images/cover.png">
		</div>
		<div class="titleData">
			Title ID: ${bookAuditDetail.titleId}<br/>
			Name: ${bookAuditDetail.proviewDisplayName}<br/>
		</div>
	</div>
</c:if>