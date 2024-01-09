function submitFormAndDisableButton(formId, button) {
	button.disabled = true;
    $('#' + formId).submit();
}

function isNotSpringGeneratedUncheckedCheckboxParameter(param) {
    return param.name.charAt(0) !== "_";
}

function submitLeftFormAndBodyForm() {
    let query = getLeftFormBodyFormQuery();
    window.location.href = window.location.href.split('?')[0] + (query ? '?' + $.param(query) : '');
}

function submitLeftFormAndBodyFormDownloadExcel(endpoint) {
    let query = getLeftFormBodyFormQuery();
    window.location.href = endpoint + (query ? '?' + $.param(query) : '');
}

function getLeftFormBodyFormQuery () {
    return $('#bodyForm, #leftForm').serializeArray().filter(function(param) {
        return param.value && isNotSpringGeneratedUncheckedCheckboxParameter(param);
    });
}

function isEmpty(value) {
    return value === null || value === '';
}

function getFilteredQueryString(json) {
    for (let key in json) {
        if (isEmpty(json[key])) {
            delete json[key];
        }
    }
    let query = $.param(json);
    return query ? '?' + query : '';
}

function submitEmptyLeftFormAndBodyForm() {
    $(':input', '#leftForm')
        .not(':button, :hidden')
        .val('')
        .prop('checked', false)
        .prop('selected', false);
    submitLeftFormAndBodyForm();
}
