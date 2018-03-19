<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:import href="../footnotesUtil.xsl"/>
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:param name="mainFile" />
    <xsl:variable name="main" select="document($mainFile)" />
    
    <xsl:template match="node()|@*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="#current" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="x:pagebreak">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
        
        <xsl:call-template name="addContentOfFootnotesWhichDefinedOnDifferentPage">
            <xsl:with-param name="pagebreakOnMainFile" select="$main//x:pagebreak[@num=current()/@num]" />
            <xsl:with-param name="pagebreakOnFootnotesFile" select="." />
            <xsl:with-param name="onPagebreak" select="true()" />
            <xsl:with-param name="currentSection" select="preceding::x:sectionbreak[1]/@sectionuuid" />
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="x:sectionbreak">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
        
        <xsl:variable name="pagebreak" select="preceding::x:pagebreak[1]" />
        <xsl:if test="$pagebreak">
            <xsl:call-template name="addContentOfFootnotesWhichDefinedOnDifferentPage">
                <xsl:with-param name="pagebreakOnMainFile" select="$main//x:pagebreak[@num=$pagebreak/@num]" />
                <xsl:with-param name="pagebreakOnFootnotesFile" select="$pagebreak" />
                <xsl:with-param name="onPagebreak" select="false()" />
                <xsl:with-param name="currentSection" select="@sectionuuid" />
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="addContentOfFootnotesWhichDefinedOnDifferentPage">
        <xsl:param name="pagebreakOnMainFile" />
        <xsl:param name="pagebreakOnFootnotesFile" />
        <xsl:param name="onPagebreak" />
        <xsl:param name="currentSection" />
        
        <xsl:variable name="xrefs" select="x:getXrefsOnGivenPageOfMainFile($pagebreakOnMainFile)" />
        <xsl:variable name="footnotes" select="x:getFootnotesOnGivenPageOfFootnoteFile($pagebreakOnFootnotesFile)" />
        
        <xsl:variable name="pbOrSecInFootnotesFile" select="current()" />
        
        <xsl:for-each select="$xrefs">
            <xsl:variable name="xref" select="current()" />
            <xsl:variable name="footnote" select="$footnotes[@id=$xref/@id]" />
            
            <xsl:if test="not($xref/@hidden) and not($footnote)">
                
                <!-- TODO: if missing footnote not after but before given xref. Detect such case and use select="$pagebreakOnFootnotesFile/preceding::x:footnote[@origId=current()/@origId][1]"-->
                <xsl:variable name="missingFootnote" select="$pagebreakOnFootnotesFile/following::x:footnote[@origId=current()/@origId][1]" />
                
                <xsl:variable name="xrefSection" select="$xref/preceding::x:sectionbreak[1]/@sectionuuid" />
                <xsl:variable name="pagebreakSection" select="$pagebreakOnMainFile/preceding::x:sectionbreak[1]/@sectionuuid" />
                
                <xsl:variable name="xrefAndPagebreakInTheSameSection" select="$xrefSection = $pagebreakSection" />
                <xsl:variable name="xrefAndCurrentInTheSameSection" select="$xrefSection = $currentSection" />
                
                <xsl:variable name="shouldAddHereMissingFootnote" select="$xrefAndCurrentInTheSameSection=true()
                and ($xrefAndPagebreakInTheSameSection=true() and $onPagebreak=true() or
                     $xrefAndPagebreakInTheSameSection!=true() and $onPagebreak!=true())" />
                     
                <xsl:if test="$shouldAddHereMissingFootnote">
                    <xsl:call-template name="addHiddenFootnote">
                        <xsl:with-param name="footnote" select="$missingFootnote"/>
                        <xsl:with-param name="pageNum" select="$pagebreakOnFootnotesFile/@num"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:function name="x:isXrefAndPagebreakInTheSameSection">
        <xsl:param name="xref" as="node()" />
        <xsl:param name="pagebreak" as="node()" />
        
        <xsl:variable name="xrefSection" select="$xref/preceding::x:sectionbreak[1]/@sectionuuid" />
        <xsl:variable name="pagebreakSection" select="$pagebreak/preceding::x:sectionbreak[1]/@sectionuuid" />
        
        <xsl:copy-of select="$xrefSection = $pagebreakSection"/>
    </xsl:function>
    
    <xsl:template name="addHiddenFootnote">
        <xsl:param name="footnote" />
        <xsl:param name="pageNum" />
        
        <xsl:element name="footnote">
            <xsl:attribute name="hidden" select="true()"/>
            <xsl:attribute name="id" select="concat($footnote/@origId, '-', $pageNum)"/>
            <xsl:apply-templates select="$footnote/x:footnote.body[1]" mode="hidden"/>
        </xsl:element>
        
    </xsl:template>
    
    <xsl:template match="x:footnote.reference" mode="hidden" />
    
    <xsl:template match="x:footnote.body" mode="hidden">
        <xsl:variable name="footnoteReference" select="ancestor::x:footnote[1]/x:footnote.reference[1]" />
    
        <xsl:element name="footnote.reference">
            <xsl:attribute name="class" select="'show_in_main_and_footnotes'"/>
            <xsl:apply-templates select="$footnoteReference/*" />
        </xsl:element>
        <xsl:element name="footnote.body">
            <xsl:attribute name="class" select="'show_in_main'"/>
            <xsl:apply-templates select="node()|@*" mode="fullContent" />
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:pagebreak | x:columns | x:column | x:endcolumn | x:endcolumns | x:page.number" mode="fullContent" />
    
    <xsl:template match="x:footnote[@orig=true()]">
        <xsl:variable name="pageNum" select="preceding::x:pagebreak[1]/@num" />
        <xsl:variable name="pagebreakMain" select="$main//x:pagebreak[@num=$pageNum]" />
    
        <xsl:choose>
            <xsl:when test="x:hasReferenceOnSamePageInMainContent(current(), $pagebreakMain)=false()">
                <xsl:variable name="xrefPreceding" select="$pagebreakMain/preceding::x:xref[@origId=current()/@origId and @orig=true()]" />
                <xsl:choose>
                    <xsl:when test="not($xrefPreceding)">
                        <xsl:variable name="xrefFollowing" select="$pagebreakMain/following::x:xref[@origId=current()/@origId and @orig=true()]" />
                        <xsl:call-template name="addFootnoteWithReferenceToDifferentPage">
                            <xsl:with-param name="xrefOnDifferentPageId" select="$xrefFollowing" />
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="addFootnoteWithReferenceToDifferentPage">
                            <xsl:with-param name="xrefOnDifferentPageId" select="$xrefPreceding" />
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node()|@*" />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="addFootnoteWithReferenceToDifferentPage">
        <xsl:param name="xrefOnDifferentPageId" />
        
        <xsl:variable name="xrefSection" select="$xrefOnDifferentPageId/preceding::x:sectionbreak[1]/@sectionuuid" />
        
        <xsl:element name="footnote">
            <xsl:attribute name="referenceOnDifferentPageId" select="true()" />
            <xsl:attribute name="xrefId" select="$xrefOnDifferentPageId/@id" />
            <xsl:attribute name="xrefDoc" select="$xrefOnDifferentPageId/preceding::x:sectionbreak[1]/@sectionuuid" />
            <xsl:apply-templates select="node()|@*" />
        </xsl:element>
    </xsl:template>
    
    <xsl:function name="x:hasReferenceOnSamePageInMainContent">
        <xsl:param name="footnote" as="node()"/>
        <xsl:param name="pagebreakMain" as="node()"/>

        <xsl:variable name="xrefs" select="x:getXrefsOnGivenPageOfMainFile($pagebreakMain)" />
        
        <xsl:copy-of select="count($xrefs[@origId=$footnote/@origId and @orig=true()])!=0" />
    </xsl:function>
    
    <xsl:function name="x:findOriginalReferenceInMainContent">
        <xsl:param name="footnote" as="node()"/>
        <xsl:param name="pagebreakMain" as="node()"/>
        
        <xsl:variable name="xref" select="$pagebreakMain/preceding::x:xref[@origId=$footnote/@origId and @orig=true()]" />
        
        <xsl:choose>
            <xsl:when test="not($xref)">
                <xsl:copy-of select="$pagebreakMain/following::x:xref[@origId=$footnote/@origId and @orig=true()]" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="$xref" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

</xsl:stylesheet>