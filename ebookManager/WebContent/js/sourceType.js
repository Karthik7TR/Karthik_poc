$(function() {
	
	var getCwbFolders = function(folderName) {
		return $.ajax({
			url: "/ebookManager/codesWorkbenchFolders.mvc?folderName=" + folderName
			}).then(function(data) {
				$("#cwbFolderList").empty();
				if(folderName) {
					var form = $("<form>").attr("id","cwbForm").attr("novalidate", "novalidate").attr("method", "get").attr("action", "foo.html");
					$("#cwbFolderList").append(form);

					var table = $("<table>").attr("id", "cwbTable");
					$(form).append(table);
					
					var tbody = $("<tbody>");
					$(table).append(tbody);
					
					$(tbody).append($("<tr>").append($("<td>").append($("<label>").html("Extract Name")))
							.append($("<td>").attr("class", "bookName").append($("<input>").attr("id", "bookName").attr("name", "bookName").attr("value", folderName).attr("readonly", "readonly")))
							.append($("<td>").attr("class", "bookName-error")));
					$(tbody).append($("<tr>").append($("<td>").html("&nbsp;")));
					$(tbody).append($("<tr>").append($("<td>").html("&nbsp;"))
							.append($("<td>").html("Sort Order (ascending)")));
					for(var index = 0; index < data.length; index++) {
						var sortInput = $("<input>").attr("id", "item-contentSet-" + index)
							.attr("name", "item-contentSet-" + index).attr("class", "contentSet").attr("contentSet", data[index]);
						// Insert value when only 1 item
						if(data.length == 1) {
							$(sortInput).val(1);
						}
						
						$(tbody).append($("<tr>").append($("<td>").append($("<label>").html("Content Set: " + data[index])))
								.append($("<td>").attr("class", "contentSet").append(sortInput))
								.append($("<td>").attr("class", "contentSet-error")));
					}
					
					var tfoot = $("<tfoot>");
					$(table).append(tfoot);
					$(tfoot).append($("<tr>").append($("<td>").append($("<button>").attr("type","button").addClass("back").html("back")))
							.append($("<td>").append($("<input>").attr("type","submit").addClass("submit").val("save"))));
					
					var validator = $("#cwbForm").validate({
						success: "valid",
						submitHandler: function(form) {
							clearCodesWorkbenchFileDetails();
							addNasInformation(form);
							$("#codesWorkbenchFolder").dialog( "close" );
						},
						errorPlacement: function(error, element) {
							error.appendTo(element.parent().next());
						}
					});
					
					var numberOfInputs = 0;
					var inputs = $('#cwbForm td.contentSet input');
					$(inputs).each(function(index) {
						numberOfInputs++;
						$(this).rules("add", {
							required: true,
							digits: true,
							maxlength: 5
						});
					});
					// show error message if CWB extract has no content set
					if(numberOfInputs == 0) {
						var hiddenInput = $("<input>").attr("id", "contentSet").attr("name", "contentSet").attr("type", "hidden");
						$(tbody).append($("<tr>").append($("<td>").append(hiddenInput))
								.append($("<td>").attr("class", "contentSet-error")));
						validator.showErrors({
							"contentSet": "At least one content set is required.  This extraction has no content set."
						});
					}
				} else {
					$("#cwbFolderList").append($("<h4>").html("Extract Name(s)"));
					for(var index = 0; index < data.length; index++) {
						var bookTitle = $("<div>").attr("bookName", data[index]).addClass("cwbFolderBookName");
						bookTitle.append($("<label>").html(data[index]));
						$("#cwbFolderList").append(bookTitle);
					}
				}
			});
	};
	
	// Add CWB File information to book definition form
	var addNasInformation = function(form) {
		
		// Add CWB book name to ebook definition form
		var bookName = $("#cwbForm input#bookName").val();
		var bookNameInput =	$("<input>").attr("id", "codesWorkbenchBookName").attr("name", "codesWorkbenchBookName")
							.attr("type", "hidden").attr("value", bookName);
		$("#codesWorkbenchBookNameValue").append(bookNameInput).append(bookName);
		
		// sort content set based on user input
		var contentSets = $("#cwbForm .contentSet input");
		contentSets.sort(function(a,b) {
			var an = $(a).val(), bn = $(b).val();
			if(an > bn) {
				return 1;
			} else if( an < bn) {
				return -1;
			} else {
				return 0;
			}
		});
		// Add content set to ebook definition form
		contentSets.each(function(index) {
			var id = "nortFileLocations" + index;
			var name = "nortFileLocations[" + index + "]";
			var locationName = $("<input>").attr("type","hidden").attr("id",id +".locationName").attr("name", name + ".locationName").attr("value", $(this).attr("contentSet"));
			var sequenceBox = $("<input>").attr("type","hidden").addClass("sequence").attr("id",id +".sequenceNum").attr("name", name + ".sequenceNum").attr("value", index + 1);
			
			var dynamicRow = $("<div>").attr("class", "dynamicRow");
			$(dynamicRow).append($("<span>").html($(this).attr("contentSet"))).append(locationName);
			
			var expandingBox = $("<div>").attr("class", "expandingBox");
			$(expandingBox).append(sequenceBox).append($("<button>").attr("type","button").attr("class","moveUp").html("Up"))
				.append($("<button>").attr("type","button").attr("class","moveDown").html("Down")).append(dynamicRow);
			
			$("#addNortFileLocationHere").append(expandingBox);
		});
	};
	
	var clearCodesWorkbenchFileDetails = function() {
		// Remove the element
		var cwbBookNameValue = $("#codesWorkbenchBookNameValue");
		$(cwbBookNameValue).siblings("div.errorDiv").empty();
		$("#addNortFileLocationHere").siblings("div.errorDiv").empty();
		$(cwbBookNameValue).empty();
		$("#codesWorkbenchBookName").remove();
		$("#addNortFileLocationHere").empty();
	};
	
	var updateSourceType = function(sourceType) {
		$("#displayTOC").hide();
		$("#displayNORT").hide();
		$("#displayFILE").hide();
		$("#displayXPP").hide();
		$("#displayFinalStage").hide();
		
		if(sourceType == "TOC") {
			$("#displayTOC").show();
			$("#displayFinalStage").show();
			$("#nortFilterView").val("");
			$("#nortDomain").val("");
			$("input:radio[name=useReloadContent][value=false]").attr('checked', true);
			clearCodesWorkbenchFileDetails();
		} else if(sourceType == "NORT") {
			$("#displayNORT").show();
			$("#displayFinalStage").show();
			$("#rootTocGuid").val("");
			$("#tocCollectionName").val("");
			$("#docCollectionName").val("");
			clearCodesWorkbenchFileDetails();
		} else if(sourceType == "XPP") {
			$("#displayXPP").show();
			$("#rootTocGuid").val("");
			$("#tocCollectionName").val("");
			$("#docCollectionName").val("");
			$("#nortFilterView").val("");
			$("#nortDomain").val("");
		} else {
			$("#displayFILE").show();
			$("#rootTocGuid").val("");
			$("#tocCollectionName").val("");
			$("#docCollectionName").val("");
			$("#nortFilterView").val("");
			$("#nortDomain").val("");
		}
	};
	
	$.validator.addMethod("contentSet", function(value, element) {
		var parentForm = $(element).closest('form');
        var timeRepeated = 0;
        $(parentForm.find('td.contentSet input')).each(function () {
            if ($(this).val() === value) {
                timeRepeated++;
            }
        });
        if (timeRepeated === 1 || timeRepeated === 0) {
            return true;
        } else { 
            return false;
        }
	}, "Please enter a unique value");

	$(document).ready(function() {
		$("#codesWorkbenchFolder").on("click", "button.back", function() {
			getCwbFolders("");
		});
		
		
		$("#codesWorkbenchFolder").on("click", "div.cwbFolderBookName", function() {
			var bookName = $(this).attr("bookName");
			getCwbFolders(bookName);
		});
		
		// Determine to show sourceType
		$('input:radio[name=sourceType]').change(function () {
			updateSourceType($(this).val());
		});
		
		updateSourceType($('input:radio[name=sourceType]:checked').val());
		
		// delete confirmation box
		$("#cwbFolderClearButton").on("click", function () {
			$("#deleteMessage").html("");
			$( "#delete-confirm" ).dialog({
				autoOpen: false,
				resizable: false,
				height:260,
				width:500,
				title: "Delete Codes Workbench Entries?",
				modal: true,
				draggable:false,
				buttons: {
					"Delete": function() {
						clearCodesWorkbenchFileDetails();
						$( this ).dialog( "close" );
					},
					Cancel: function() {
						$( this ).dialog( "close" );
					}
				}
			});
			$( "#delete-confirm" ).dialog( "open" );
		});
		
		
		// Save codes workbench settings
		$("#cwbFolderButton").on("click", function () {
			getCwbFolders("");
			
			$( "#codesWorkbenchFolder" ).dialog({
				autoOpen: false,
				resizable: false,
				height:350,
				width:600,
				title: "Codes Workbench Folder",
				modal: true,
				draggable:false,
				buttons: {
					Cancel: function() {
						$( this ).dialog( "close" );
					}
				}
			});
			$( "#codesWorkbenchFolder" ).dialog( "open" );
		});
	});
});
