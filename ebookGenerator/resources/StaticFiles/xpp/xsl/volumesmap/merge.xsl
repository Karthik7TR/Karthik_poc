<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
	<xsl:param name="tag-name" />

	<xsl:template match="/">
		<xsl:element name="{$tag-name}">
			<xsl:apply-templates select=".//x:entry" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="x:entry">
		<xsl:copy-of select="." />
	</xsl:template>
</xsl:stylesheet>