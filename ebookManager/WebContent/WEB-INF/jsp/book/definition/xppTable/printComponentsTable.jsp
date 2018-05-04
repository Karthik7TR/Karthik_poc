<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<script src="js/jsgrid/db.js"></script>
<script src="js/jsgrid/src/jsgrid.core.js"></script>
<script src="js/jsgrid/src/jsgrid.load-indicator.js"></script>
<script src="js/jsgrid/src/jsgrid.load-strategies.js"></script>
<script src="js/jsgrid/src/jsgrid.sort-strategies.js"></script>
<script src="js/jsgrid/src/jsgrid.field.js"></script>
<script src="js/jsgrid/src/fields/jsgrid.field.text.js"></script>
<script src="js/jsgrid/src/fields/jsgrid.field.control.js"></script>
<script src="js/jsgrid/src/jsgrid.validation.js"></script>
<script src="js/htmlEnDeCode.js"></script>
<script src="js/printComponents/printComponentsTable.js"></script>

<div id="${param.jsGridId}" class="jsGrid-container"></div>

<input type='hidden' id='${param.jsGridId}PrintComponentsData' ${param.edit != null && param.edit ? 'name="printComponents"' : ''}
    value='${param.printComponents}'>

<script>
    new PrintComponentsTable(
            "#${param.jsGridId}",
            "${param.edit}",
            "${param.superUserParam}",
            "${param.colorPrintComponentTable}",
            "#${param.editBookDefinitionFormId}"
        );
</script>
