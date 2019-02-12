function submitFormAndDisableButton(formId, button) {
	button.disabled = true;
    $('#' + formId).submit();
}
