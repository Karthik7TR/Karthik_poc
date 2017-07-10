<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">

    <xsl:template match="x:sectionbreak">
        <xsl:call-template name="insertCloseTags" />
        <xsl:copy-of select="."/>
        <xsl:call-template name="insertOpenTags" />
    </xsl:template>
    
    <xsl:template name="insertCloseTags">
        <xsl:for-each select="ancestor::node()">
            <xsl:sort select="position()" data-type="number" order="descending" />
            <xsl:if test="local-name(.) != '' and local-name(.) != 'root'">
                <xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
                <xsl:value-of select="local-name(.)" />
                <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="insertOpenTags">
        <xsl:for-each select="ancestor::node()">
            <xsl:if test="local-name(.) != '' and local-name(.) != 'root'">
                <xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
                <xsl:value-of select="local-name(.)" />
                <xsl:for-each select="./@*">
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="local-name(.)" />
                    <xsl:text disable-output-escaping="yes"><![CDATA[="]]></xsl:text>
                    <xsl:value-of select="." />
                    <xsl:text disable-output-escaping="yes"><![CDATA["]]></xsl:text>
                </xsl:for-each>
                <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>