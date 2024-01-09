<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:include href="createMap.xsl" />
	
	<xsl:template match="x:item">
		<xsl:copy-of select="." />
	</xsl:template>
	
</xsl:stylesheet>