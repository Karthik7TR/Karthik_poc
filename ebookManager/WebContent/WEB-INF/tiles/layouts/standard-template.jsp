<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
	<title>Ebook Manager</title>
  </head>
  	
  <body>
	<table style="width:100%;">
	  <tr style="background:yellow;">
	    <td colspan="2" style="height:40px;">
	      <tiles:insertAttribute name="header"/>
	    </td>
	  </tr>
	  <tr>
	    <td style="width:25%; background:cyan;">
	      <tiles:insertAttribute name="left"/>
	    </td>
	    <td>
	      <table style="width:100%;">
	    	<tr>
	      	  <td style="background:lightgray; height:100px"><tiles:insertAttribute name="top"/></td>
	    	</tr>
	    	<tr>
	      	  <td style="background:orange;"><tiles:insertAttribute name="body"/></td>
	    	</tr>
	      </table>
	    </td>
	  </tr>
	  <tr>
	    <td colspan="2" style="height:40px; background:red;">
	      <tiles:insertAttribute name="footer"/>
	    </td>
	  </tr>
	</table>
  </body>
</html>