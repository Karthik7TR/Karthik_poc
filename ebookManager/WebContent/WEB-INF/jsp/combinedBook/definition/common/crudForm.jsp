<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script src="js/jsgrid/db.js"></script>
<script src="js/jsgrid/src/jsgrid.core.js"></script>
<script src="js/jsgrid/src/jsgrid.load-indicator.js"></script>
<script src="js/jsgrid/src/jsgrid.load-strategies.js"></script>
<script src="js/jsgrid/src/jsgrid.sort-strategies.js"></script>
<script src="js/jsgrid/src/jsgrid.field.js"></script>
<script src="js/jsgrid/src/fields/jsgrid.field.text.js"></script>
<script src="js/jsgrid/src/fields/jsgrid.field.control.js"></script>
<script src="js/jsgrid/src/jsgrid.validation.js"></script>
<script src="js/combined-book-definition-grid.js"></script>

<form:hidden path="id" />
<form:hidden path="deletedFlag" />
<div id="grid-container">
    <div id="jsGrid"></div>
    <input id="combined-book-definition-sources" type="hidden" name= "sources" value="${combinedBookDefinitionForm.sources}">
    <div class="combined-book-definition-errors">
        <form:errors cssClass="errorMessage" />
    </div>
</div>
