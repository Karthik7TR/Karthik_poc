function formatDateFromUTCString(utcStr){
    var options = {
            hour: "numeric",
            minute: "numeric",
            second: "numeric",
            month: "numeric",
            year:"numeric",
            day: "numeric",
            hour12: false
        };
    return new Date(utcStr).toLocaleDateString("en-US",options).replace(',','');
};

function formatDates() {
	$('.toFormatDate').each(function(){
		$(this).text(formatDateFromUTCString($(this).text().trim()));
	});
}