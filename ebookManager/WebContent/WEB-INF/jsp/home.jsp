
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<html>
<head>
</head>

<body>
<h2>eBook Manager Home</h2>

<a href="<%= WebConstants.MVC_BOOK_LIBRARY_LIST %>">Book Library List</a><br/>

<a href="book/definition/view.mvc">Book Definition View</a><br/>

<a href="stub.mvc">Stub Tiles Layout Page</a><br/>
<br/>

</body>
</html>
