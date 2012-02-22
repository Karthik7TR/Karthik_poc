<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
  	<link rel="stylesheet" href="theme/jquery-ui-1.8.17.custom.css" />
	<link rel="stylesheet" href="/ebookManager/theme/layout.css"/>
	<link rel="stylesheet" href="/ebookManager/theme/displaytag-table.css"/>
  	
  	<script src="js/jquery-1.7.1.min.js" type="text/javascript"> </script>
  	<script type="text/javascript" src="js/jquery-1.7.1.js"></script>
  	<script type="text/javascript" src="js/jquery-ui-1.8.17.custom.js"></script>
  	
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