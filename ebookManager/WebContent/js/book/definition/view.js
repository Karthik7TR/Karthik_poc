function toggleNotes(notesDiv) {
	const notesId = notesDiv.id + "-quote";
	const notes = $("#" + notesId);
	const height = notes.css("max-height");
	if (height === "0px") {
		notes.css("max-height", "1000px");
		notes.css("overflow-x", "auto");
	} else {
		notes.css("max-height", "0px");
		notes.css("overflow-x", "hidden");
	}
}