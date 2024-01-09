function requestDataFromSap() {
    let subNumber = $('#printSubNumber').val();
    if (subNumber == "") {
        alert("Please fill \"Print Sub Number\" field");
        return;
    }
    let requestButton = $('#performSapRequest');
    let saveButton = $('#confirm');
    let validateButton = $('#validate');

    if (confirm("All data in print components table will be overwritten, continue?")) {
        disableButtons(requestButton, saveButton, validateButton);
        let spinner = new Spinner(document.getElementById('jsGrid'));

        //A dirty hack to start animation on dynamically created element
        setTimeout(function() {
            window.activeSapRequest = $.ajax({
                url: "/ebookManager/getDataFromSap.mvc",
                type: "Post",
                data: { "subNumber": subNumber, "setNumber": $('#printSetNumber').val(), "titleId": $('#titleIdBox').val() },
                dataType: "json",
                success: function (response) {
                    let grid = $('#jsGrid');
                    var data = response.materialComponents;
                    restoreUi(requestButton, saveButton, validateButton, spinner);
    
                    if (data.length == 0) {
                        alert(response.message);
                        return;
                    }
    
                    if (isSameData(grid, data)) {
                        alert('There is no changes in Print components table');
                        return;
                    }
    
                    clearGrid(grid);
                    for (let index = 0; index < data.length;) {
                        let currentData = data[index];
                        grid.jsGrid("insertItem", {
                            componentOrder: ++index,
                            materialNumber: currentData.bom_component,
                            componentName: currentData.material_desc
                        });
                    }
                },
                error: function (jqXHR, status) {
                    restoreUi(requestButton, saveButton, validateButton, spinner);
                    if (status !== 'abort') {
                        alert("Unknown error");
                    }
                },
                beforeSend: function () { spinner.spin(); },
            });
        }, 30);
    }
    
    function restoreUi(requestButton, saveButton, validateButton, spinner) {
    	enableButtons(requestButton, saveButton, validateButton);
    	spinner.stop();
    }

    function disableButtons(requestButton, saveButton, validateButton) {
        requestButton.attr('disabled', 'disabled');
        saveButton.attr('disabled', 'disabled');
        validateButton.attr('disabled', 'disabled');
        saveButton.addClass('lightgray');
        validateButton.addClass('lightgray');
    }

    function enableButtons(requestButton, saveButton, validateButton) {
        requestButton.removeAttr('disabled');
        saveButton.removeAttr('disabled');
        validateButton.removeAttr('disabled');
        saveButton.removeClass('lightgray');
        validateButton.removeClass('lightgray');
    }
};