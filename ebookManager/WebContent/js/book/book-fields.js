function hideFields(sourceType) {
    if (sourceType === 'XPP') {
        $('.xppHideClass').css('display', 'none');
    }
    if (sourceType === 'FILE') {
        $('.cwbHideClass').css('display', 'none');
    }
}