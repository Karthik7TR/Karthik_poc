<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    
    <xsl:template match="x:tbl">
        <xsl:choose>
            <xsl:when test=".//x:pagebreak">
                <xsl:element name="tbl">
                    <xsl:apply-templates select="@*" />
                    <xsl:attribute name="mode" select="'scroll_mode'" />
                    <xsl:element name="table">
                        <xsl:apply-templates select="./x:table[1]/@*" />
                        <xsl:apply-templates select="./x:table[1]/x:colgroup" />
                        <xsl:apply-templates select="./x:table[1]/x:thead" mode="tableInScrollMode" />
                        <xsl:element name="tbody">
                            <xsl:for-each select="./x:table/x:tbody/x:row">
                                <xsl:apply-templates select="." mode="tableInScrollMode" />
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
                <xsl:element name="tbl">
                    <xsl:apply-templates select="@*" />
                    <xsl:attribute name="mode" select="'page_mode'" />
                    <xsl:apply-templates />
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node()|@*" />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="node()|@*" mode="tableInScrollMode" >
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="#current" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="x:xref[@type='footnote']" mode="tableInScrollMode">
        <xsl:element name="xref">
            <xsl:attribute name="id" select="concat(@id,'-scrollmode')" />
            <xsl:attribute name="type" select="@type" />
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:page.number.ref" mode="tableInScrollMode" />
    
</xsl:stylesheet>