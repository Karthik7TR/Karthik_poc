<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.WebConstants"%>
<%@page import="com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm"%>

<div class="generateTitleID">
	
</div>
<div class="leftDefinitionForm">
	<div class="row">
		<form:label path="titleId" class="labelCol">Title ID</form:label>
		<form:input path="titleId" disabled="true" />
	</div>
	<div class="row">
		<form:label path="bookName" class="labelCol">Book Name</form:label>
		<form:input path="bookName" />
	</div>
	<div class="row">
		<form:label path="majorVersion" class="labelCol">Major Version</form:label>
		<form:input path="majorVersion" />
	</div>
	<div class="row">
		<form:label path="minorVersion" class="labelCol">Minor Version</form:label>
		<form:input path="minorVersion" />
	</div>
	<div class="row">
		<form:label path="keywords" class="labelCol">Keywords</form:label>
		<form:input path="keywords" />
	</div>
	<div class="row">
		<form:label path="type" class="labelCol">Type</form:label>
		<form:input path="type" />
	</div>
	<div class="row">
		<form:label path="value" class="labelCol">Value</form:label>
		<form:input path="value" />
	</div>
	<div class="row">
		<form:label path="copyright" class="labelCol">Copyright</form:label>
		<form:input path="copyright" />
	</div>
	<div class="row">
		<form:label path="materialId" class="labelCol">Material ID</form:label>
		<form:input path="materialId" />
	</div>
	<div class="row">
		<form:label path="authorInfo" class="labelCol">Author Information</form:label>
		<form:input path="authorInfo" />
	</div>
	<div class="row">
		<form:label path="tocCollectionName" class="labelCol">TOC Collection</form:label>
		<form:input path="tocCollectionName" />
	</div>
	<div class="row">
		<form:label path="rootTocGuid" class="labelCol">Root TOC Guid</form:label>
		<form:input path="rootTocGuid" />
	</div>
	<div class="row">
		<form:label path="nortDomain" class="labelCol">NORT Domain</form:label>
		<form:input path="nortDomain" />
	</div>
	<div class="row">
		<form:label path="nortFilterView" class="labelCol">NORT Filter View</form:label>
		<form:input path="nortFilterView" />
	</div>
</div>

<div class="rightDefinitionForm">
	<div class="row">
		<form:label path="coverImage" class="labelCol">Cover Image</form:label>
		<form:input path="coverImage" />
	</div>
	<div class="row">
		<form:label path="isbn" class="labelCol">ISBN</form:label>
		<form:input path="isbn" />
	</div>
	<div class="row">
		<form:label path="autoUpdateSupport" class="labelCol">Auto Update Support</form:label>
		<form:radiobutton path="autoUpdateSupport" value="true" />True
		<form:radiobutton path="autoUpdateSupport" value="false" />False
	</div>
	<div class="row">
		<form:label path="searchIndex" class="labelCol">Search Index</form:label>
		<form:radiobutton path="searchIndex" value="true" />True
		<form:radiobutton path="searchIndex" value="false" />False
	</div>
	<div class="row">
		<form:label path="onePassSSOLinking" class="labelCol">One Pass SSO Linking</form:label>
		<form:radiobutton path="onePassSSOLinking" value="true" />True
		<form:radiobutton path="onePassSSOLinking" value="false" />False
	</div>
	<div class="row">
		<form:label path="language" class="labelCol">Language</form:label>
		<form:input path="language" />
	</div>
	<div class="row">
		<form:label path="imageView" class="labelCol">Image View</form:label>
		<form:radiobutton path="imageView" value="true" />True
		<form:radiobutton path="imageView" value="false" />False
	</div>
	<div class="row">
		<form:label path="imageCollectionInformation" class="labelCol">Image Collection</form:label>
		<form:input path="imageCollectionInformation" />
	</div>
	<div class="row">
		<form:label path="nameSpacePubId" class="labelCol">Name Space Pub ID</form:label>
		<form:input path="nameSpacePubId" />
	</div>
	<div class="row">
		<form:label path="currency" class="labelCol">Currency</form:label>
		<form:input path="currency" />
	</div>
</div>

