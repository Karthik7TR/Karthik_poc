<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>

<script type="text/javascript">
    const proviewAuditFilterParams = {
        titleId: '${ currentSessionUserPreferences.proviewAuditPreferences.titleId }',
        username: '${ currentSessionUserPreferences.proviewAuditPreferences.username }',
        requestFromDateString: '${ currentSessionUserPreferences.proviewAuditPreferences.requestFromDateString }',
        requestToDateString: '${ currentSessionUserPreferences.proviewAuditPreferences.requestToDateString }',
        action: '${ currentSessionUserPreferences.proviewAuditPreferences.action }',
        objectsPerPage: '${ currentSessionUserPreferences.proviewAuditPreferences.objectsPerPage }',
        page: '${ currentSessionUserPreferences.proviewAuditPreferences.page }',
        sort: '${ currentSessionUserPreferences.proviewAuditPreferences.sort }',
        dir: '${ currentSessionUserPreferences.proviewAuditPreferences.dir }'
    };
    const proviewAuditQueryString = $.param(proviewAuditFilterParams);
    $(window).on('pageshow', function () {
        $('#proviewAuditLink').prop('href', '${ WebConstants.MVC_PROVIEW_AUDIT_LIST }' + '?' + proviewAuditQueryString);
    });
</script>

<div class="pageTitle">Latest ProView Title Info</div>
<br/>
<a id="proviewAuditLink" href="<%= WebConstants.MVC_PROVIEW_AUDIT_LIST %>">ProView Audit List</a>
