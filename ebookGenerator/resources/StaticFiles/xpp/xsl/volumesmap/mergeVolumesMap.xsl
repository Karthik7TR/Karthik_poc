<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<VolumesMap>
			<xsl:apply-templates select=".//x:entry" />
		</VolumesMap>
	</xsl:template>
	
	<xsl:template match="x:entry">
		<xsl:copy-of select="." />
	</xsl:template>
</xsl:stylesheet>