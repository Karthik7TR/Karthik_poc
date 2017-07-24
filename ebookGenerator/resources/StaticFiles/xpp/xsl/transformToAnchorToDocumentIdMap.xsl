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
	
	<xsl:template match="x:XPPHier">
		<xsl:element name="item">
			<xsl:variable name="uuid" select="./@uuid" />
			<xsl:attribute name="key" select="$uuid" />
			<xsl:value-of>
				<xsl:choose>
					<xsl:when test="following::x:XPPMetaData[@parent_uuid = $uuid]">
						<xsl:value-of select="following::x:XPPMetaData[@parent_uuid = $uuid][1]/@md.doc_family_uuid" />
					</xsl:when>
					<xsl:when test="preceding::x:XPPMetaData[1]/@md.doc_family_uuid">
						<xsl:value-of select="preceding::x:XPPMetaData[1]/@md.doc_family_uuid" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="following::x:XPPMetaData[1]/@md.doc_family_uuid" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:value-of>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="text()" />
</xsl:stylesheet>