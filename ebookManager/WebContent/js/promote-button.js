let checkedItemsInReviewCounter = 0;

function changePromoteButtonVisibilityAfterSelectAllCheckboxStatusChange(checkboxes, promoteButtonCanBeEnabled) {
    if (promoteButtonCanBeEnabled === 'true') {
        resetCheckedItemsInReviewCounter();
        addAllCheckedItemsWithReviewStatusToCounter(checkboxes);
        changePromoteButtonVisibility();
    }
}

function addAllCheckedItemsWithReviewStatusToCounter(checkboxes) {
    for (let index = 0; index < checkboxes.length; index++) {
        if (isNotSelectAllCheckbox(checkboxes[index])) {
            addCheckedItemWithReviewStatusToCounter(checkboxes[index]);
        }
    }
}

function resetCheckedItemsInReviewCounter() {
    if ($('#selectAll').prop('checked')) {
        checkedItemsInReviewCounter = 0;
        if ($('#groupChecked').prop('checked') && isReviewStatus($('#groupStatusValue').text())) {
            checkedItemsInReviewCounter++;
        }
    }
}

function isNotSelectAllCheckbox(checkbox) {
    return checkbox.id !== 'selectAll';
}

function changePromoteButtonVisibilityAfterBookCheckboxStatusChange(checkbox, promoteButtonCanBeEnabled) {
	if (promoteButtonCanBeEnabled === 'true') {
        addCheckedItemWithReviewStatusToCounter(checkbox);
        changePromoteButtonVisibility();
	}
	updateSelectAll(checkbox);
}

function addCheckedItemWithReviewStatusToCounter(checkbox) {
	const bookStatuses = findStatusesOfBookParts(checkbox);
	for (let index = 0; index < bookStatuses.length; index++) {
		const checkedItemStatus = bookStatuses[index].innerText;
		updateCheckedItemsInReviewCounterValue(checkbox, checkedItemStatus);
	}
}
function findStatusesOfBookParts(checkbox) {
    return checkbox.parentNode.parentNode.querySelectorAll('.bookStatus');
}

function changePromoteButtonVisibility() {
	if (checkedItemsInReviewCounter > 0) {
		$('#promoteButton').prop('disabled', false)
			.removeClass('ui-button-disabled')
			.removeClass('ui-state-disabled');
	} else {
		$('#promoteButton').prop('disabled', true)
			.addClass('ui-button-disabled')
			.addClass('ui-state-disabled');
	}
}

function changePromoteButtonVisibilityAfterGroupCheckboxStatusChange(checkbox, promoteButtonCanBeEnabled) {
    if (promoteButtonCanBeEnabled === 'true') {
        updateCheckedItemsInReviewCounterValue(checkbox, $('#groupStatusValue').text());
        changePromoteButtonVisibility();
	}
}

function updateCheckedItemsInReviewCounterValue(checkbox, itemStatus) {
	if (isReviewStatus(itemStatus)) {
		if (checkbox.checked) {
			checkedItemsInReviewCounter++;
		} else {
			if (checkedItemsInReviewCounter > 0) {
				checkedItemsInReviewCounter--;
			}
		}
	}
}

function isReviewStatus(itemStatus) {
    return itemStatus === 'Review';
}
