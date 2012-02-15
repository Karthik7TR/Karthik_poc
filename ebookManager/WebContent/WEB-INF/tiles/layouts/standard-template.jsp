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
	<link rel="stylesheet" href="/ebookManager/theme/displaytag-table.css"/>
  	
  	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js" type="text/javascript"> </script>
  	<script type="text/javascript" src="js/jquery.js"></script>
  	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
  	
	<title>eBook Manager</title>
  </head>
  	
  <body>
  		<div id="fullContainer">
			<div id="header">
				<tiles:insertAttribute name="header"/>
			</div>
			
			<div id="bodyContainer">
				<div class="colright">
					<div class="col1wrap">
						<div class="col1">
							<div class="title">
								<tiles:insertAttribute name="title"/>
							</div>
						
							<div class="mainContent">
								<div class="mainWrapper">
									<tiles:insertAttribute name="body"/>
								</div>
							</div>
						</div>
					</div>
					<div class="col2">
						<div class="leftWrapper">
							<tiles:insertAttribute name="left"/>
						</div>
					</div>
				</div>
			</div>
			
			<div id="footer">
				<tiles:insertAttribute name="footer"/>
			</div>
		</div>
  </body>
</html>