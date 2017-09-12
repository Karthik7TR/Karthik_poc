<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:template match="x:pagebreak">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
        <xsl:call-template name="addReferenceToSplitByPageFootnotePart">
            <xsl:with-param name="pageNum" select="@num" />
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="addReferenceToSplitByPageFootnotePart">
        <xsl:param name="pageNum" />

        <xsl:variable name="splitFootnoteId">
            <xsl:variable name="footnotePagebreak"
                select="$footnotesDocument//x:pagebreak[@num=$pageNum]" />
            <xsl:value-of select="$footnotePagebreak/ancestor::x:footnote[1]/@id" />
        </xsl:variable>

        <xsl:if test="$splitFootnoteId != ''">
            <xsl:element name="xref">
                <xsl:attribute name="id"
                    select="concat($splitFootnoteId, '-', $pageNum)" />
                <xsl:attribute name="type" select="'footnote'" />
                <xsl:attribute name="split" select="true()" />
            </xsl:element>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>