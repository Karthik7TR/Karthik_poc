const SHOW_OPTION = "true";
const HIDE_OPTION = "false";
const USE_CUT_OFF_DATE = "usecutoffdate";
function hideFields(sourceType) {
    if (sourceType === 'XPP') {
        $('.xppHideClass').css('display', 'none');
    }
    if (sourceType === 'FILE') {
        $('.cwbHideClass').css('display', 'none');
    }
}
function updatePubCutoffDateBox(sourceType) {
    let showPubCutoffDate = getContentTypeIdElement().attr(USE_CUT_OFF_DATE);
    let displayPubCutoffDateOptions = $("#displayPubCutoffDateOptions");
    if(showPubCutoffDate === SHOW_OPTION && sourceType !== "XPP") {
        displayPubCutoffDateOptions.show();
        updatePubCutoffDate(getPublicationCutoffDateUsedValue());
    } else {
        displayPubCutoffDateOptions.hide();
        uncheckPublicationCutoffDateUsed()
        updatePubCutoffDate(false);
    }
}
function updatePubCutoffDate(showPubCutoffDate) {
    let displayCutoffDate = $("#displayCutoffDate");
    displayCutoffDate.hide();
    if(showPubCutoffDate === SHOW_OPTION) {
        displayCutoffDate.show();
    }
}
function getContentTypeIdElement() {
    let element = $('#contentTypeId :selected');
    if(element.length === 0) {
        element = $('#contentTypeId');
    }
    return element;
}
function uncheckPublicationCutoffDateUsed() {
    getPublicationCutoffDateUsed(SHOW_OPTION).prop("checked", false);
    getPublicationCutoffDateUsed(HIDE_OPTION).prop("checked", true);
}
function getPublicationCutoffDateUsed(value) {
    return $("input[name=publicationCutoffDateUsed][value=" + value + "]");
}
function getPublicationCutoffDateUsedValue() {
    return $('input:radio[name=publicationCutoffDateUsed]:checked').val();
}