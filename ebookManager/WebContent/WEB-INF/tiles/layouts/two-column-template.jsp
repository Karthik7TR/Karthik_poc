<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel="stylesheet" href="theme/layout.css"/>
    <link rel="stylesheet" href="theme/displaytag-table.css"/>

    <link rel="stylesheet" type="text/css" href="js/jsgrid/css/jsgrid.css" />
    <link rel="stylesheet" type="text/css" href="js/jsgrid/css/theme.css" />
	<link rel="icon" href="favicon.ico" type="image/x-icon">

    <link rel="stylesheet" href="theme/jquery-ui-1.12.1.css" />
    <link rel="stylesheet" href="theme/jquery-ui.custom.css" />
   
    <script type="text/javascript" src="js/jquery-1.12.4.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.12.1.js"></script>
    <script type="text/javascript" src="js/jquery-ui-timepicker-addon.js"></script>
    <script type="text/javascript" src="js/textboxHint.js"></script>
    <script type="text/javascript">
	  $(document).ready(function() {
				<%-- Style buttons with jquery  --%>
				$( ".buttons input:submit,.buttons input:button,.buttons a,.buttons button,.modalButtons button,.modalButtons a" ).button();
	  });
    </script>
	<title>eBook Manager</title>
  </head>
  	
  <body>
	  <div id="wrapper">
			<div id="header">
				<tiles:insertAttribute name="header"/>
				<div style="clear:both;"></div>
			</div>
			<div id="center">
				<div id="right">
					<tiles:insertAttribute name="outageHeader"/>
					<div class="titleWrapper">
						<div class="title">
							<tiles:insertAttribute name="title"/>
						</div>
					</div>
				
					<div class="main">
						<tiles:insertAttribute name="body"/>
					</div>
				</div>
				<div id="left">
					<tiles:insertAttribute name="left"/>
				</div>
			</div>
			<div id="push"></div>
		</div>
		<div id="footerWrapper">
			<div id="footer">
				<tiles:insertAttribute name="footer"/>
			</div>
		</div>
  </body>
</html>