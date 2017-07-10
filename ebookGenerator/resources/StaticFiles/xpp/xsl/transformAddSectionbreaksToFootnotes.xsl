<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:param name="mainDocumentWithSectionbreaks" />
    <xsl:variable name="main" select="document($mainDocumentWithSectionbreaks)" />

    <xsl:variable name="firstFootnote" select=".//x:footnote[1]" />

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="x:pagebreak">
        <xsl:variable name="isInsideFootnote" select="ancestor::x:footnote" />

        <xsl:if test="not($isInsideFootnote)">
            <xsl:call-template name="addSectionbreakByPageNumber">
                <xsl:with-param name="pageNumber" select="@num" />
            </xsl:call-template>
        </xsl:if>

        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="x:footnote">
        <xsl:variable name="innerPagebreak" select="(self::node()//x:pagebreak)[1]" />

        <xsl:if test="$innerPagebreak">
            <xsl:call-template name="addSectionbreakByPageNumber">
                <xsl:with-param name="pageNumber" select="$innerPagebreak/@num" />
            </xsl:call-template>
        </xsl:if>

        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template name="addSectionbreakByPageNumber">
        <xsl:param name="pageNumber" />

        <xsl:variable name="mainPagebreak" select="$main//x:pagebreak[@num=$pageNumber]" />
        <xsl:variable name="sectionbreak" select="$mainPagebreak/preceding::x:sectionbreak[1]" />

        <xsl:if test="count($sectionbreak/following::x:pagebreak intersect $mainPagebreak/preceding::x:pagebreak)=0">
            <xsl:copy-of select="$sectionbreak" />
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>