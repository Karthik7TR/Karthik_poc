<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
	xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="text" indent="no" omit-xml-declaration="yes" />

	<!--Identity transformation, which turns entities into characters-->
	<xsl:template match="x:document">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:choose>
			<xsl:when test="self::text()">
				<xsl:value-of select="." disable-output-escaping="yes" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates select="@*|node()" />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
