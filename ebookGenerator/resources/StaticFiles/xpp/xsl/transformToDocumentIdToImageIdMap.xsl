<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:x="http://www.w3.org/1999/xhtml" exclude-result-prefixes="x">
    <xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
    
    <xsl:template match="x:body">
        <xsl:value-of select="concat(@fileBaseName, '|')" />
        <xsl:apply-templates></xsl:apply-templates>
        <xsl:text>&#xd;&#xa;</xsl:text>
    </xsl:template>
    
    <xsl:template match="x:img[@assetid]">
        <xsl:value-of select="concat(substring-after(@assetid,'#'), ',')" />
    </xsl:template>

    <xsl:template match="text()"></xsl:template>
</xsl:stylesheet>