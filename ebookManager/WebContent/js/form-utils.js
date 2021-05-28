function submitFormAndDisableButton(formId, button) {
	button.disabled = true;
    $('#' + formId).submit();
}

function submitLeftFormAndBodyForm() {
    let query = $('#bodyForm, #leftForm').serialize();
    window.location.href = window.location.href.split('?')[0] + '?' + query;
}

function submitEmptyLeftFormAndBodyForm() {
    $(':input', '#leftForm')
        .not(':button, :hidden')
        .val('')
        .prop('checked', false)
        .prop('selected', false);
    submitLeftFormAndBodyForm();
}
