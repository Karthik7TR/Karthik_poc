$(function() {
	$(document).ready(function() {
		const CHAR_CODE_0 = 48;
		const CHAR_CODE_9 = 57;
		
		// Add Comment
		$('#confirm').click(function(e) {
		    //Cancel the link behavior
		    e.preventDefault();
		 
		    //Get the screen height and width
		    var maskHeight = $(document).height();
		    var maskWidth = $(window).width();
		 
		    //Set height and width to mask to fill up the whole screen
		    $('#mask').css({'width':maskWidth,'height':maskHeight});
		     
		    //transition effect 
		    $('#mask').fadeTo("fast",0.5);  
		 
		    //Get the window height and width
		    var winH = $(window).height();
		    var winW = $(window).width();
		           
		    //Set the popup window to center
		    $('#dialog').css('top',  winH/2-$('#dialog').height()/2);
		    $('#dialog').css('left', winW/2-$('#dialog').width()/2);
		 
		    //transition effect
		    $('#dialog').fadeIn(500);
		    $('#validateForm').val(false);
		});
		
		$('#dialog .save').click(function () {
			warning= false;
		});
		
		//if close button is clicked
		$('#dialog .closeModal').click(function (e) {
		    //Cancel the link behavior
		    e.preventDefault();
		    $('#mask, .window').hide();
		});
		
		//if mask is clicked
	    $('#mask').click(function () {
	        $(this).hide();
	        $('.window').hide();
	    });
	    
	    this.bookDefinitionIdField = {
	        addRestrictions: function(element) {
	            if (!this.hasKeypressAndChangeHandlers(element)) {
	                $(element).on("keypress", function(event) {
	                    return event.charCode >= CHAR_CODE_0 && event.charCode <= CHAR_CODE_9;
	                });
	                $(element).on("change", function() {
	                    element.value = element.value.replace(/[^0-9]/g, "");
	                });
	            }
	        },
	        hasKeypressAndChangeHandlers: function (element) {
	            var ev = $._data(element, 'events');
	            return ev && ev.keypress && ev.change;
	        }
	    }
	});
});