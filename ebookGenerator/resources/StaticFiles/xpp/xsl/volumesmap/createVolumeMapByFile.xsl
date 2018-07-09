<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" />
	
	<xsl:param name="defaultVolumeNumber" />
	<xsl:param name="volumeNumberByFileName" />
	<xsl:param name="segOutlineFilePath" />
	<xsl:param name="segOutlineFile" select="document($segOutlineFilePath)" />

	<xsl:template match="/">
		<VolumesMap>
			<xsl:apply-templates />
		</VolumesMap>
	</xsl:template>
	
	<xsl:template match="x:XPPHier">
		<xsl:variable name="uuid" select="./@uuid" />
		<xsl:element name="entry">
			<xsl:attribute name="uuid" select="$uuid" />
			<xsl:choose>
				<xsl:when test="$volumeNumberByFileName">
					<xsl:value-of select="$volumeNumberByFileName" />
				</xsl:when>
				<xsl:when test="$segOutlineFilePath">
					<xsl:value-of select="$segOutlineFile/root//object[@uuid = $uuid]/@volume" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$defaultVolumeNumber" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>