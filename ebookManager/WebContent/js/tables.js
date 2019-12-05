function updateSelectAll(checkbox) {
    if (!checkbox.checked) {
        $('#selectAll').prop('checked', false);
    } else {
        checkSelectAllIfAllCheckboxesAreChecked();
    }
}

function checkSelectAllIfAllCheckboxesAreChecked() {
    var checkboxes = document.querySelectorAll('.simple-checkbox');
    for (var i = 0; i < checkboxes.length; i++) {
        if (!checkboxes[i].checked) {
            return;
        }
    }
    $('#selectAll').prop('checked', true);
}
