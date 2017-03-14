<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x="http://www.w3.org/1999/xhtml" exclude-result-prefixes="x">
    <xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
	
    <xsl:template match="x:body">
        <xsl:apply-templates>
            <xsl:with-param name="fileBaseName" select="@fileBaseName"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="x:a[@name]">
        <xsl:param name="fileBaseName" />
        <xsl:value-of select="concat(@name, '|', $fileBaseName, '&#xd;&#xa;')" />
	</xsl:template>

    <xsl:template match="text()"></xsl:template>
</xsl:stylesheet>