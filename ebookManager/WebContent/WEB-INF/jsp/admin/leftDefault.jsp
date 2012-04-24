<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<h3><a href="<%= WebConstants.MVC_ADMIN_MAIN %>">Admin</a></h3>

Drop-down Menu
<ul>
	<li><a href="<%= WebConstants.MVC_ADMIN_JURIS_CODE_VIEW %>">Jurisdiction</a></li>
	<li><a href="<%= WebConstants.MVC_ADMIN_PUBLISH_TYPE_CODE_VIEW %>">Publish Type</a></li>
	<li><a href="<%= WebConstants.MVC_ADMIN_STATE_CODE_VIEW %>">States</a></li>
</ul>

ProView
<ul>
	<li><a href="<%= WebConstants.MVC_ADMIN_KEYWORD_CODE_VIEW %>">Keywords</a></li>
</ul>

Book Definition
<ul>
	<li><a href="<%= WebConstants.MVC_ADMIN_BOOK_LOCK_LIST %>">Locks</a></li>
</ul>

<a href="<%=WebConstants.MVC_ADMIN_STOP_GENERATOR%>">Stop Generator and Gatherer</a>