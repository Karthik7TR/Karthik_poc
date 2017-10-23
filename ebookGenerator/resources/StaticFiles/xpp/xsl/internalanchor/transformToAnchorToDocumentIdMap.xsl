<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:include href="createMap.xsl"/>

	<xsl:template match="x:XPPHier">
		<xsl:call-template name="create-item">
			<xsl:with-param name="uid" select="@uuid" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="x:XPPMetaData">
		<xsl:call-template name="create-item">
			<xsl:with-param name="uid" select="@guid" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>