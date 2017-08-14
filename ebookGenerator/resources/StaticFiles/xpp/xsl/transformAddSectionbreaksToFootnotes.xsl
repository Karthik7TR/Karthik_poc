<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
	xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:param name="mainDocumentWithSectionbreaks" />
    <xsl:variable name="main" select="document($mainDocumentWithSectionbreaks)" />

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="x:footnote">
        <xsl:call-template name="addSectionbreak">
            <xsl:with-param name="mainNode" select="$main//x:xref[@id=current()/@id]" />
        </xsl:call-template>
        
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="x:pagebreak">
        <xsl:variable name="outerFootnote" select="ancestor::x:footnote[1]" />

        <xsl:choose>
            <xsl:when test="$outerFootnote">
                <xsl:call-template name="insertCloseTags" />
                <xsl:call-template name="processPagebreak" />
                <xsl:call-template name="insertOpenTags" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="processPagebreak" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="processPagebreak">
        <xsl:call-template name="addSectionbreak">
            <xsl:with-param name="mainNode" select="$main//x:pagebreak[@num=current()/@num]" />
        </xsl:call-template>
        
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template name="addSectionbreak">
        <xsl:param name="mainNode" />
        
        <xsl:variable name="sectionbreak" select="$mainNode/preceding::x:sectionbreak[1]" />

        <xsl:variable name="noPreceidingXrefs" select="count($sectionbreak/following::x:xref intersect $mainNode/preceding::x:xref)=0" />
        <xsl:variable name="noPreceidingPagebreaks" select="count($sectionbreak/following::x:pagebreak intersect $mainNode/preceding::x:pagebreak)=0" />

        <xsl:if test="$noPreceidingXrefs and $noPreceidingPagebreaks">
            <xsl:copy-of select="$sectionbreak" />
        </xsl:if>
    </xsl:template>
    
    <xsl:function name="x:pagebreak-and-footnote-from-different-sections">
		<xsl:param name="footnote" as="node()" />
        <xsl:param name="pagebreak" as="node()" />
        
        <xsl:variable name="footnoteRef" select="$footnote/@id"/>
        <xsl:variable name="mainXref" select="$main//x:xref[@id=$footnoteRef]" />
        <xsl:variable name="footnoteSectionbreak" select="$mainXref/preceding::x:sectionbreak[1]" />
        
        <xsl:variable name="pageNumber" select="$pagebreak/@num"/>
        <xsl:variable name="mainPagebreak" select="$main//x:pagebreak[@num=$pageNumber]" />
        <xsl:variable name="pageSectionbreak" select="$mainPagebreak/preceding::x:sectionbreak[1]" />
        
		<xsl:sequence select="$footnoteSectionbreak/@sectionuuid != $pageSectionbreak/@sectionuuid" />
	</xsl:function>
    
    <xsl:template name="insertCloseTags">
        <xsl:for-each select="ancestor::node()">
            <xsl:sort select="position()" data-type="number" order="descending" />
            <xsl:if test="local-name(.) != '' and (ancestor::x:footnote or local-name(.) = 'footnote')">
                <xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
                <xsl:value-of select="local-name(.)" />
                <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="insertOpenTags">
        <xsl:for-each select="ancestor::node()">
            <xsl:variable name="isFootnote" select="local-name(.) = 'footnote'" />
            <xsl:if test="local-name(.) != '' and (ancestor::x:footnote or $isFootnote)">
                <xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
                <xsl:value-of select="local-name(.)" />
                <xsl:if test="not($isFootnote)">
                    <xsl:for-each select="./@*">
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="local-name(.)" />
                        <xsl:text disable-output-escaping="yes"><![CDATA[="]]></xsl:text>
                        <xsl:value-of select="." />
                        <xsl:text disable-output-escaping="yes"><![CDATA["]]></xsl:text>
                    </xsl:for-each>
                </xsl:if>
                <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>