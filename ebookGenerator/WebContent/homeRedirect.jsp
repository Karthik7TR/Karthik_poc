<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%--
	Redirect user to the main home page controller.
	Done because the web.xml welcome-file-list element must be a file resource. 
--%>
<html>
<body>

	<c:redirect url="/home.mvc"/>

</body>
</html>