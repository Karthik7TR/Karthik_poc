<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<link rel="stylesheet" href="/ebookManager/theme/layout.css"/>
	<title>eBook Manager</title>
  </head>
  	
  <body>
  	<div class="header">
		<tiles:insertAttribute name="header"/>
	</div>
	
	<div class="bodyContainer">
		<div class="left">
			<tiles:insertAttribute name="left"/>
		</div>
		
		<div class="title">
			<tiles:insertAttribute name="title"/>
		</div>
		
		<div class="body">
			<tiles:insertAttribute name="body"/>
		</div>
	</div>
	
	<div class="footer">
		<tiles:insertAttribute name="footer"/>
	</div>

  </body>
</html>