<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<script src="js/printComponents/printComponentsHistoryPanelUtil.js"></script>
<script>
    var historyPanel = new PrintComponentsHistoryPanelUtils(
            "${param.printComponentsHistoryLastVersionNumber}",
            "#${param.openCloseCompareButton}",
            "#${param.leftPanelFields}",
            "#${param.leftPanelPrintComponentsCompareFeature}",
            "${param.bookdefinitionId}",
            "#printComponentsHistoryVersionsDropdown",
            "#historyJsGrid",
            "<%= WebConstants.MVC_PRINT_COMPONENTS_HISTORY_VERSION %>"
            );
</script>
<div id="leftPanelPrintComponentsCompareFeature" class="leftDefinitionForm historyPrintComponentsPanel" 
        panelOpened="false" historyLoaded="false">
    <h3>Print components history</h3>
    <div>
        <h3>Version:
            <select id="printComponentsHistoryVersionsDropdown" name="department" onchange="historyPanel.onSelectAnotherVersion(this)">
                <c:forEach var="item" items="${param.printComponentsHistoryVersions}">
                    <c:set var='version' value='${item.replaceAll("[\\\\[ \\\\]]","")}' />
                    <option value='${version}' ${status.first ? 'selected="selected"' : ''}>${version}</option>
                </c:forEach>
            </select>
        </h3>
    </div>
    <div id="historyTable">
        <jsp:include page="printComponentsTable.jsp" >
            <jsp:param name="jsGridId" value="historyJsGrid"/>
            <jsp:param name="edit" value="false"/>
            <jsp:param name="superUserParam" value="false"/>
            <jsp:param name="printComponents" value="[]"/>
            <jsp:param name="colorPrintComponentTable" value="false"/>
        </jsp:include>
    </div>
    <div style="width:100%; float:right; align:right; margin-top:1em;">
       <input type="button" value="Close panel" onclick="historyPanel.openClosePrintComponentsComparePanel()"/>
    </div>
</div>