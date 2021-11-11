function dialog(btnId, btnLabel, httpMethod, url, redirectUrl) {
    $("#dialog-confirm").dialog({
        autoOpen: false,
        resizable: false,
        height: 260,
        width: 500,
        modal: true,
        draggable: false,
        buttons: {
            [btnLabel]: function () {
                $(".ui-dialog-buttonset .ui-button:contains('" + btnLabel + "')").prop('disabled', true);
                document.body.style.cursor='wait'
                $.ajax({
                    url: url,
                    type: httpMethod,
                    success: function () {
                        document.body.style.cursor='default'
                        location.href = redirectUrl;
                    }
                });
            },
            Cancel: function () {
                $(this).dialog("close");
            }
        }
    });
    $(btnId).click(function () {
        $("#dialog-confirm").dialog("open");
    });
}
