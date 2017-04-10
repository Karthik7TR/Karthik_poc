<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="Document">
        <map>
            <xsl:apply-templates />
        </map>
    </xsl:template>
    
    <xsl:template match="x:part.main">
        <xsl:apply-templates>
            <xsl:with-param name="fileName" select="@fileName" />
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="x:topic/x:front/x:outline.name.block[@toc.include='y']" >
        <xsl:param name="fileName" />
        <xsl:variable name="uuid" select="../../@uuid" />
        
        <xsl:call-template name="mapEntry">
            <xsl:with-param name="key" select="$uuid" />
            <xsl:with-param name="uuid" select="$uuid" />
            <xsl:with-param name="value" select="$fileName" />
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="x:chapter/x:front/x:outline.name.block[@toc.include='y'] | x:analytical.level/x:front/x:outline.name.block[@toc.include='y']" >
        <xsl:param name="fileName" />
        <xsl:variable name="uuid" select="../../@uuid" />
        
        <xsl:call-template name="mapEntry">
            <xsl:with-param name="key" select="$uuid" />
            <xsl:with-param name="uuid" select="$uuid" />
            <xsl:with-param name="designator" select="x:designator/text()" />
            <xsl:with-param name="value" select="$fileName" />
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="x:section/x:section.front/x:outline.name.block[@toc.include='y']" >
        <xsl:param name="fileName" />
        <xsl:variable name="uuid" select="../../@uuid" />
        
        <xsl:call-template name="mapEntry">
            <xsl:with-param name="key" select="x:designator/text()" />
            <xsl:with-param name="uuid" select="$uuid" />
            <xsl:with-param name="designator" select="x:designator/text()" />
            <xsl:with-param name="value" select="$fileName" />
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="mapEntry" >
        <xsl:param name="key" />
        <xsl:param name="uuid" />
        <xsl:param name="designator" />
        <xsl:param name="value" />
        
        <xsl:element name="entry">
            <xsl:attribute name="key" >
                <xsl:value-of select="$key" />
            </xsl:attribute>
            <xsl:attribute name="uuid" >
                <xsl:value-of select="$uuid" />
            </xsl:attribute>
            <xsl:attribute name="designator" >
                <xsl:value-of select="$designator" />
            </xsl:attribute>
            <xsl:value-of select="$value" />
        </xsl:element>
    </xsl:template>

    <xsl:template match="text()" mode="#all" />
</xsl:stylesheet>