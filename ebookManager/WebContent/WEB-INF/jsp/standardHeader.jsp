<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.UserUtils" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="userFullName" value="<%=UserUtils.getAuthenticatedUserFullName()%>"/>

<script type="text/javascript">
    const groupListFilterParams = {
        groupFilterName: '${ currentSessionUserPreferences.groupFilterName }',
        groupFilterId: '${ currentSessionUserPreferences.groupFilterId }'
    };
    const groupListQueryString = $.param(groupListFilterParams);

    const proviewListFilterParams = {
        proviewDisplayName: '${ currentSessionUserPreferences.proviewDisplayName }',
        titleId: '${ currentSessionUserPreferences.titleId }',
        minVersions: '${ currentSessionUserPreferences.minVersions }',
        maxVersions: '${ currentSessionUserPreferences.maxVersions }',
        objectsPerPage: '${ currentSessionUserPreferences.proviewListObjectsPerPage }'
    };
    const proviewListQueryString = $.param(proviewListFilterParams);

    $(window).on('pageshow', function () {
        $('#groupListTab').prop('href', '${ WebConstants.MVC_PROVIEW_GROUPS }' + '?' + groupListQueryString);
        $('#proviewListTab').prop('href', '${ WebConstants.MVC_PROVIEW_TITLES }' + '?' + proviewListQueryString);
    });
</script>

<div class="headerLeftSide">
	<span class="programTitle">THOMSON REUTERS <span style="color:orange">eBook Manager</span></span>  <span class="welcomeMessage"> - Welcome ${userFullName}</span>
	<c:if test="${not fn:containsIgnoreCase('prod', environmentName) }">
		<span class="environment">Environment: ${environmentName}</span>
	</c:if>
</div>

<div class="headerRightSide">
	<ul class="navList">
		<li><a id="libraryTab" href="<%=WebConstants.MVC_BOOK_LIBRARY_LIST%>">LIBRARY</a></li>
		<li><a id="proviewListTab" href="<%=WebConstants.MVC_PROVIEW_TITLES%>">PROVIEW LIST</a></li>
		<li><a id="AuditTab" href="<%=WebConstants.MVC_BOOK_AUDIT_LIST%>">AUDIT</a></li>
		<li><a id="jobsTab" href="<%=WebConstants.MVC_JOB_SUMMARY%>">JOBS</a></li>
		<li><a id="queuedTab" href="<%=WebConstants.MVC_JOB_QUEUE%>">QUEUED</a></li>
		<li><a id="groupListTab" href="<%=WebConstants.MVC_PROVIEW_GROUPS%>">GROUP LIST</a></li>

		<%-- Check if user has role to access create book definition --%>
		<c:set var="showCreate" value="false"/>
		<sec:authorize access="hasAnyRole('ROLE_PUBLISHER,ROLE_SUPERUSER,ROLE_PUBLISHER_PLUS')">
			<c:set var="showCreate" value="true"/>
		</sec:authorize>
		<c:if test="${showCreate}">
			<li><a id="createBookTab" href="<%=WebConstants.MVC_BOOK_DEFINITION_CREATE%>">CREATE BOOK</a></li>
			<li><a id="createCombinedBookTab" href="<%=WebConstants.MVC_COMBINED_BOOK_DEFINITION_CREATE%>">CREATE COMBINED BOOK</a></li>
		</c:if>

		<li><a href="<%=WebConstants.MVC_USER_PREFERENCES%>">PREFERENCES</a></li>

		<%-- Check if user has role to access Admin --%>
		<c:set var="showAdmin" value="false"/>
		<sec:authorize access="hasAnyRole('ROLE_SUPPORT,ROLE_SUPERUSER')">
			<c:set var="showAdmin" value="true"/>
		</sec:authorize>
		<c:if test="${showAdmin}">
			<li><a id="adminTab" href="<%=WebConstants.MVC_ADMIN_MAIN%>">ADMIN</a></li>
		</c:if>
		<li><a id="statsTab" href="<%=WebConstants.MVC_STATS%>">STATS</a></li>
		<li><a id="supportLinksTab" href="<%=WebConstants.MVC_SUPPORT_PAGE_VIEW%>">SUPPORT LINKS</a></li>
		<li><a id="logoutTab" href="j_spring_security_logout">LOGOUT</a></li>
	</ul>
</div>
