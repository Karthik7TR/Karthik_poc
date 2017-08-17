<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

	<xsl:template match="x:proview_image">
		<xsl:variable name="guid" select="substring-before(@id,'.')" />
		<xsl:element name="img">
			<xsl:attribute name="class" select="'tr_image'" />
			<xsl:attribute name="assetid" select="concat('er:#', $guid)" />
			<xsl:attribute name="src" select="concat('er:#', $guid)" />
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>