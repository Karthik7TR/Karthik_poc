<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
	<xsl:include href="createMap.xsl" />
	<xsl:include href="../transform-utils.xsl" />
	<xsl:param name="isPocketPart" />

	<xsl:template match="x:XPPHier">
		<xsl:call-template name="create-item">
			<xsl:with-param name="uid">
				<xsl:value-of select="x:process-id(@uuid, $isPocketPart)" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="x:XPPMetaData">
		<xsl:call-template name="create-item">
			<xsl:with-param name="uid">
				<xsl:value-of select="x:process-id(@guid, $isPocketPart)" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
    
    <xsl:template match="x:XPPSummaryTOCAnchor">
        <xsl:call-template name="create-item">
            <xsl:with-param name="uid">
				<xsl:value-of select="x:process-id(@uuid, $isPocketPart)" />
			</xsl:with-param>
            <xsl:with-param name="type" select="'sumtoc'" />
        </xsl:call-template>
    </xsl:template>

</xsl:stylesheet>