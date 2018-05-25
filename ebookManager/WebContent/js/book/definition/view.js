function toggleNotes() {
	var height = $(".notes-quote").css("max-height");
	if (height === "0px") {
		$(".notes-quote").css("max-height", "1000px");
	} else {
		$(".notes-quote").css("max-height", "0px");
	}
}