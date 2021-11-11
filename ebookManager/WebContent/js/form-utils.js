function submitFormAndDisableButton(formId, button) {
	button.disabled = true;
    $('#' + formId).submit();
}

function submitLeftFormAndBodyForm() {
    let query = $('#bodyForm, #leftForm').serializeArray().filter(function(param) {
        return param.value;
    });
    window.location.href = window.location.href.split('?')[0] + (query ? '?' + $.param(query) : '');
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
