function formatDateFromUTCString(utcStr){
    var options = {
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit",
            month: "2-digit",
            year:"numeric",
            day: "2-digit",
            hour12: false
        };
    return new Date(utcStr).toLocaleDateString("en-US",options).replace(',','');
};

function fixIEHiddenSymbols(text){
    return text.replace(/[^0-9 :/+a-zA-Z\(\)]/g, "");
};

function formatDates() {
	$('.toFormatDate').each(function(){
		$(this).text(formatDateFromUTCString($(this).text().trim()));
	});
}

function validateDate(selector) {
	const dateTextBox = $(selector);
	try {
		startDateISOString = $.datepicker.parseDateTime("mm/dd/yy", "HH:mm:ss", dateTextBox.val(), null).toISOString();
		dateTextBox.val(startDateISOString);
	} catch(e) {
		dateTextBox.val("");
	}
}