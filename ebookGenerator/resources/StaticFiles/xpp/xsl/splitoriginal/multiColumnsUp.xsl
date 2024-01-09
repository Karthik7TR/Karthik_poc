<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.sdl.com/xpp"
    xmlns="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:import href="moveElementUpUtil.xsl" />

    <xsl:template match="x:columns">
        <xsl:call-template name="insertCloseTags" />
        <xsl:text disable-output-escaping="yes"><![CDATA[<columns>]]></xsl:text>        
    </xsl:template>
    
    <xsl:template match="x:column">
        <xsl:text disable-output-escaping="yes"><![CDATA[<column>]]></xsl:text>
        <xsl:call-template name="insertOpenTags" />
    </xsl:template>
    
    <xsl:template match="x:endcolumn">
        <xsl:call-template name="insertCloseTags" />
        <xsl:text disable-output-escaping="yes"><![CDATA[</column>]]></xsl:text>
    </xsl:template>
    
    <xsl:template match="x:endcolumns">
        <xsl:text disable-output-escaping="yes"><![CDATA[</columns>]]></xsl:text>
        <xsl:call-template name="insertOpenTags" />
    </xsl:template>
    
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>