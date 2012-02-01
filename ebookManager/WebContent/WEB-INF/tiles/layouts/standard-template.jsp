<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
	<title>eBook Manager</title>
  </head>
  	
  <body>
	<table cellpadding="0" cellspacing="0" style="width:100%;">
	  <tr style="background:black;">
	    <td colspan="2">
	      <tiles:insertAttribute name="header"/>
	    </td>
	  </tr>
	  <tr>
	    <td style="width:25%; background:#F8F8F8;">
	      <tiles:insertAttribute name="left"/>
	    </td>
	    <td>
	      <table cellpadding="0" cellspacing="0" style="width:100%;">
	    	<tr>
	      	  <td style="background:#DBDDDD; height:100px"><tiles:insertAttribute name="title"/></td>
	    	</tr>
	    	<tr>
	      	  <td style="background:white;"><tiles:insertAttribute name="body"/></td>
	    	</tr>
	      </table>
	    </td>
	  </tr>
	  <tr>
	    <td colspan="2" style="height:40px; background:lightgray;">
	      <tiles:insertAttribute name="footer"/>
	    </td>
	  </tr>
	</table>
  </body>
</html>