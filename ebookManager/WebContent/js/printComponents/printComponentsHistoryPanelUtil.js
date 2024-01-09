function PrintComponentsHistoryPanelUtils(
        lastVersionNumber,
        openCloseButtonId,
        leftPanelFieldsId,
        leftPanelCompareFeatureId,
        bookDefinitionId,
        printComponentsHistoryVersionsDropdownId,
        historyJsGridId,
        getPrintComponentsUrl
) {
    var openCloseButton = {};
    var leftPanelFields = {};
    var leftPanelCompareFeature = {};
    var printComponentsHistoryVersionsDropdown = {};
    var historyPanel = this;
    
    $(window).load(function() {
        openCloseButton = $(openCloseButtonId);
        leftPanelFields = $(leftPanelFieldsId);
        leftPanelCompareFeature = $(leftPanelCompareFeatureId);
        printComponentsHistoryVersionsDropdown = $(printComponentsHistoryVersionsDropdownId).get(0);
        openCloseButton.on("click", historyPanel.openClosePrintComponentsComparePanel);
    });
    
    this.openClosePrintComponentsComparePanel = function() {
        var openPanel = leftPanelCompareFeature.attr("panelOpened") == "false";
        
        leftPanelCompareFeature.attr("panelOpened", openPanel);
        openCloseButton.attr("value", openPanel ? "Close print component history panel" : "Open print component history panel");
        leftPanelCompareFeature.css("display", openPanel ? "table-cell" : "none");
        leftPanelFields.css("display", openPanel ? "none" : "table-cell");
        
        if (leftPanelCompareFeature.attr("historyLoaded") == "false") {
            takePrintComponentsVersion(lastVersionNumber);
            leftPanelCompareFeature.attr("historyLoaded", true);
        }
        if (openPanel) {
            printComponentsHistoryVersionsDropdown.focus();
        }
    };
    
    this.onSelectAnotherVersion = function(dropDown) {
        var version = dropDown.options[dropDown.selectedIndex].value;
        takePrintComponentsVersion(version);
    };
    
    function takePrintComponentsVersion(version) {
        $.ajax({
            type: "POST",
            url: getPrintComponentsUrl,
            data: { 
                bookDefinitionId: bookDefinitionId,
                version: version
            },
            success: function(response) {
                $(historyJsGridId).get(0).gridManager.reloadData(response);
            },
            async: true
        });
    };
}