<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	
	<xsl:template match="x:TLRkey">
		<xsl:element name="img">
			<xsl:attribute name="alt" select="'Key Number Symbol'" />
			<xsl:attribute name="class" select="'TLRkey'" />
			<xsl:attribute name="assetid" select="'er:#blackkey'" />
			<xsl:attribute name="src" select="'er:#blackkey'" />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>