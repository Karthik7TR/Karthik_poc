<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<uuidmap>
			<xsl:apply-templates />
		</uuidmap>
	</xsl:template>

	<xsl:template name="create-item">
		<xsl:param name="uid" />
		<xsl:param name="type" select="'book'" />
		<xsl:param name="splitTitleId" />

		<xsl:element name="item">
			<xsl:attribute name="key" select="$uid" />
			<xsl:attribute name="type" select="$type" />
			
			<xsl:if test="$splitTitleId">
				<xsl:attribute name="splitTitleId" select="$splitTitleId" />
			</xsl:if>
			
			<xsl:value-of select="preceding::x:sectionbreak[1]/@sectionuuid" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()" />
	
</xsl:stylesheet>