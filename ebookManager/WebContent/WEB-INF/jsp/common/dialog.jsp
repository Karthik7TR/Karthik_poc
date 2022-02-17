<script src="js/dialog.js"></script>
<div id="dialog-confirm" title="${param.title}" style="display:hidden;">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>${param.description}</p>
</div>
<script>
    dialog('${param.btnSelector}', '${param.btnLabel}', '${param.httpMethod}', '${param.url}', '${param.redirectUrl}');
</script>
