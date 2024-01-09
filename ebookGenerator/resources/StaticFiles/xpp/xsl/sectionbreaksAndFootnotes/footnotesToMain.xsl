<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    
    <xsl:import href="../footnotesUtil.xsl"/>
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:template match="x:pagebreak">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
        <xsl:call-template name="addReferenceToSplitByPageFootnotePart">
            <xsl:with-param name="pageNum" select="@num" />
        </xsl:call-template>
        
        <xsl:call-template name="addXrefIfRequired">
            <xsl:with-param name="pageNum" select="@num" />
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="x:xref[@type='footnote']">
        <xsl:variable name="pageNum" select="preceding::x:pagebreak[1]/@num" />
        <xsl:call-template name="addFootnoteXref">
            <xsl:with-param name="id" select="@id" />
            <xsl:with-param name="suffix" select="$pageNum" />
            <xsl:with-param name="orig" select="true()" />
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
            <xsl:call-template name="addFootnoteXref">
                <xsl:with-param name="id" select="$splitFootnoteId" />
                <xsl:with-param name="suffix" select="$pageNum" />
                <xsl:with-param name="hidden" select="true()" />
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="addXrefIfRequired">
        <xsl:param name="pageNum" />

        <xsl:variable name="xrefs" select="x:getXrefsOnGivenPageOfMainFile(current())" />
        <xsl:variable name="footnotes" select="x:getFootnotesOnGivenPageOfFootnoteFile($footnotesDocument//x:pagebreak[@num=$pageNum])" />
       
       <xsl:for-each select="$footnotes">
       		<xsl:variable name="hasXrefs" select="(count($xrefs[@id=current()/@id])>0)" />
       		<xsl:variable name="shouldCopyColumn" select="x:shouldCopyColumn($footnotesDocument//x:pagebreak[@num=$pageNum])" />
            <xsl:if test="$hasXrefs=false() or $shouldCopyColumn=true()">
                <xsl:call-template name="addFootnoteXref">
                    <xsl:with-param name="id">
                    	<xsl:choose>
                    		<xsl:when test="$hasXrefs=true() and $shouldCopyColumn=true()">
                    			<xsl:value-of select="concat(@id, '-', $pageNum)" />
                    		</xsl:when>
                    		<xsl:otherwise>
                    			<xsl:value-of select="@id" />
                    		</xsl:otherwise>
                    	</xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="suffix">
                    	<xsl:choose>
                    		<xsl:when test="$hasXrefs=true() and $shouldCopyColumn=true()">
                    			<xsl:value-of select="$COLUMN_COPY" />
                    		</xsl:when>
                    		<xsl:otherwise>
                    			<xsl:value-of select="$pageNum" />
                    		</xsl:otherwise>
                    	</xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="hidden" select="true()" />
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="addFootnoteXref">
        <xsl:param name="id" />
        <xsl:param name="suffix" />
        <xsl:param name="orig" />
        <xsl:param name="hidden" />

        <xsl:element name="xref">
            <xsl:attribute name="id" select="concat($id, '-', $suffix)" />
            <xsl:attribute name="origId" select="$id" />
            <xsl:attribute name="type" select="'footnote'" />
            <xsl:if test="$hidden">
                <xsl:attribute name="hidden" select="true()" />
            </xsl:if>
            <xsl:if test="$orig">
                <xsl:attribute name="orig" select="true()" />
            </xsl:if>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>