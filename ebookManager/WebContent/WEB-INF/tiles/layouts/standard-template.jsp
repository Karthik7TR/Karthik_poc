<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<link rel="stylesheet" href="theme/jquery.ui.all.css" />
  	<link rel="stylesheet" href="theme/jquery.ui.datepicker.css" />
  	<link rel="stylesheet" href="theme/jquery.ui.theme.css" />
  	<link rel="stylesheet" href="theme/jquery.ui.core.css" />
	<link rel="stylesheet" href="theme/jquery.ui.base.css" />  	
	<link rel="stylesheet" href="/ebookManager/theme/layout.css"/>
  	
  	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type="text/javascript"> </script>
  	<script type="text/javascript" src="js/jquery.js"></script>
  	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
  	
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