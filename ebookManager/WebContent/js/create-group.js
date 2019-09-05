$(function() {
	var updateTitleFields = function() {
    	$("#notgrouped li input").each(function(idx) {
    		var groupIdx = $(this).closest("li").index();
    		var type = $(this).attr("attr");
        	$(this).attr("id", "notGrouped.titles" + groupIdx + "." + type)
        	.attr("name", "notGrouped.titles[" + groupIdx + "]." + type);
        });
    	
        $("#groups .group").each(function(idx){
        	$(this).find("li input").each(function(idx) {
        		var groupIdx = $(this).closest(".group").index();
        		var inputIdx = $(this).closest("li").index();
        		var type = $(this).attr("attr");
        		
        		$(this).attr("id", "subgroups"+ groupIdx +".titles" +  inputIdx + "." + type)
        			.attr("name", "subgroups[" + groupIdx + "].titles[" + inputIdx + "]" + "." + type);
        	});
        });
    };
    
    var updateSubgroupHeadingFields = function() {
    	$("#groups .group input.subheading").each(function(idx) {
    		$(this).attr("id", "subgroups"+ idx +".heading").attr("name", "subgroups[" + idx + "].heading");
    		$(this).prev("label").attr("for", "subgroups"+ idx +".heading").html("Subgroup " + (idx + 1) + " heading:");
    	});
    	
    };
    
    var addSubgroup = function() {
    	var groupIdx = $("#groups .group").length;
    	var group = $("<div>").addClass("group");
    	group.append($("<button>").addClass("removeSubgroup").attr("type", "button").html("Remove Subgroup").click(deleteSubgroup));
    	
    	var row = $("<div>").addClass("row");
    	row.append($("<label>").html("Subgroup "+ (groupIdx + 1) +" heading:"));
    	row.append($("<input>").attr("id", "subgroups"+ groupIdx +".heading").attr("name", "subgroups[" + groupIdx + "].heading")
    			.attr("type", "text").addClass("subheading"));
    	
    	group.append(row);
    	
    	
    	var droppableList = $("<ul>").addClass("drop");
    	group.append(droppableList);
    	
    	$("#groups").append(group);
    	
    	droppableList.sortable({
            connectWith: "ul.drop",
            placeholder: "ui-state-highlight",
            stop: function(e, ui) {
            	updateTitleFields();
            },
          }).disableSelection();
    }
    
    var determineSubgroups = function() {
    	var radio = $('input:radio[name=includeSubgroup]:checked');
    	var isSubgroup = $.trim(radio.val());
		$("#notgrouped").hide();
		$("#showPilotBooks").show();
		determinePilotBooks();
		$(".rightDefinitionForm").hide();
		
		if(isSubgroup == "true" || isSubgroup == true) {	
			$("#notgrouped").show();	
			$("#showPilotBooks").hide();
			$("#includePilotBook").val(true);
			$(".rightDefinitionForm").show();
		} 
    };
    
    var determinePilotBooks = function() {
    	var radio = $('input:radio[name=includePilotBook]:checked');
    	var pilotBooks = $.trim(radio.val());
		$("#displayPilotBooks").hide();
    	if (pilotBooks == "true" || pilotBooks == true) {
    		$("#displayPilotBooks").show();
    	}
    };

	var deleteSubgroup = function() {
		var srow = $(this).parent();
		var titles = $(this).siblings("ul").children("li");
		$( "#delete-confirm" ).dialog({
			autoOpen: false,
			resizable: false,
			height:260,
			width:500,
			title: "Delete Subgroup",
			modal: true,
			draggable:false,
			buttons: {
				"Delete": function() {
					// Remove the element
					srow.remove();
					$("#titles").append(titles);
					updateTitleFields();
					updateSubgroupHeadingFields();
					$( this ).dialog( "close" );
				},
				Cancel: function() {
					$( this ).dialog( "close" );
				}
			}
		});
		$( "#delete-confirm" ).dialog( "open" );
	}
    
	$(document).ready(function() {
		 $( "ul.drop" ).sortable({
	        connectWith: "ul.drop",
	        placeholder: "ui-state-highlight",
	        stop: function(e, ui) {
	        	updateTitleFields();
	        },
	      }).disableSelection();
		 
		 $('input:radio[name=includeSubgroup]').change(function () {
			 determineSubgroups();
		 });
		 
		 $('input:radio[name=includePilotBook]').change(function () {
			determinePilotBooks(); 
		 });
		 
		 // Setup Button Click handlers 
		 	$('#addSubgroup').click(function () {
			addSubgroup();
			
			// IE8 bug: forcing reflow/redraw to resize the parent div 
			$('#groups').hide();
			$('#groups').show();
			
			$( "ul.drop" ).sortable("refresh");
		});

		// delete confirmation box
		$(this).find(".removeSubgroup").click(deleteSubgroup);
		determineSubgroups();
		determinePilotBooks();
	});
});