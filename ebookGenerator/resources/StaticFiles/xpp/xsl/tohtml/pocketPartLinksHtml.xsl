<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

    <xsl:template match="x:referenceFromPocketPartToMain">
        <xsl:call-template name="pocketPartLink">
            <xsl:with-param name="uuid" select="@docId" />
            <xsl:with-param name="prefix" select="@docId" />
            <xsl:with-param name="linkSuffix" select="'_anchorOnMain'" />
            <xsl:with-param name="anchorSuffix" select="'_anchorOnPP'" />
            <xsl:with-param name="text">Link to Main Content</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="x:referenceFromMainToPocketPart">
        <xsl:call-template name="pocketPartLink">
            <xsl:with-param name="uuid" select="concat(@docId, '_pp')" />
            <xsl:with-param name="prefix" select="@docId" />
            <xsl:with-param name="linkSuffix" select="'_anchorOnPP'" />
            <xsl:with-param name="anchorSuffix" select="'_anchorOnMain'" />
            <xsl:with-param name="text" select="concat('Link to ', @webBuildProductType)" />
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="pocketPartLink">
        <xsl:param name="uuid" />
        <xsl:param name="prefix" />
        <xsl:param name="linkSuffix" />
        <xsl:param name="anchorSuffix" />
        <xsl:param name="text" />
        
        <xsl:element name="div">
            <xsl:attribute name="class" select="'pocketPartLinkWrapper'" />
            <xsl:element name="a">
                <xsl:attribute name="href" select="concat('er:#', $uuid, '/', $prefix, $linkSuffix)" />
                <xsl:attribute name="name" select="concat($prefix, $anchorSuffix)" />
                <xsl:attribute name="class" select="'pocketPartLink'" />
                <xsl:value-of select="$text" />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>