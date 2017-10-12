<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
    
    <xsl:function name="x:getXrefsOnGivenPageOfMainFile">
        <xsl:param name="currentPagebreakInMainFile" as="node()"/> 
        
        <xsl:variable name="nextPagebreakInMainFile" select="$currentPagebreakInMainFile/following::x:pagebreak[1]"/>
        
        <xsl:variable name="xrefs" select="$currentPagebreakInMainFile/following::x:xref[@type='footnote'] intersect $nextPagebreakInMainFile/preceding::x:xref[@type='footnote']" />
        
        <xsl:sequence select="$xrefs"/>
    </xsl:function>
    
    <xsl:function name="x:getFootnotesOnGivenPageOfFootnoteFile">
        <xsl:param name="currentPagebreakInFootnoteFile" as="node()"/> 
        
        <xsl:variable name="nextPagebreakInFootnotesFile" select="$currentPagebreakInFootnoteFile/following::x:pagebreak[1]" />
        <xsl:variable name="footnotesOnGivenPage" select="$currentPagebreakInFootnoteFile/following::x:footnote intersect (
        $nextPagebreakInFootnotesFile/preceding::x:footnote | 
        $nextPagebreakInFootnotesFile/ancestor::x:footnote)" />
        
        <xsl:sequence select="$footnotesOnGivenPage"/>
    </xsl:function>
    
    <xsl:function name="x:findFootnoteById" as="node()">
        <xsl:param name="footnoteId"/>
        <xsl:param name="currentNode" as="node()"/>
        
        <xsl:variable name="followingFootnote" select="$currentNode/following::x:footnote[@origId=$footnoteId]"/>
        
        <xsl:choose>
            <xsl:when test="not($followingFootnote)">
                <xsl:value-of select="$currentNode/preceding::x:footnote[@origId=$footnoteId]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$followingFootnote"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
</xsl:stylesheet>