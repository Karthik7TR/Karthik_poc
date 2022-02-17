function dialog(btnSelector, btnLabel, httpMethod, url, redirectUrl) {
    $("#dialog-confirm").dialog({
        autoOpen: false,
        resizable: false,
        height: 260,
        width: 500,
        modal: true,
        draggable: false,
        buttons: {
            [btnLabel]: function () {
                const confirmBtn = $(".ui-dialog-buttonset .ui-button:contains('" + btnLabel + "')")
                confirmBtn.prop('disabled', true);
                document.body.style.cursor='wait'
                $.ajax({
                    url: url,
                    type: httpMethod,
                    success: function () {
                        document.body.style.cursor='default'
                        if (redirectUrl !== '') {
                            location.href = redirectUrl;
                        } else {
                            $("#dialog-confirm").dialog("close");
                            confirmBtn.prop('disabled', false);
                        }
                    }
                });
            },
            Cancel: function () {
                $(this).dialog("close");
            }
        }
    });
    $(document).ready(function () {
        $(btnSelector).click(function () {
            $("#dialog-confirm").dialog("open");
        });
    })
}
