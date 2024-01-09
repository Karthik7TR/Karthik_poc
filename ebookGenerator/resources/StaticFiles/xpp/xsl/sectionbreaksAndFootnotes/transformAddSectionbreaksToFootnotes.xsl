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
    
    <xsl:template match="x:footnote">
        <xsl:variable name="pageNum" select="preceding::x:pagebreak[1]/@num" />
        <xsl:variable name="newId" select="concat(@id, '-', $pageNum)"/>
        <xsl:variable name="xref" select="$main//x:xref[@id=$newId and @type='footnote' and not(@hidden)]" />
        
        <xsl:if test="$xref">
            <xsl:call-template name="addSectionbreak">
                <xsl:with-param name="correspondingNodeInMain" select="$xref" />
            </xsl:call-template>
            <xsl:call-template name="addCopyOfFootnoteForTableInScrollMode">
                <xsl:with-param name="correspondingNodeInMain" select="$xref" />
            </xsl:call-template>
        </xsl:if>
        
        <xsl:element name="footnote">
            <xsl:attribute name="id" select="$newId" />
            <xsl:attribute name="origId" select="@id" />
            <xsl:attribute name="orig" select="true()" />
            <xsl:apply-templates select="node()|@*[not(name()='id')]" />
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:footnote.body">
        <xsl:choose>
            <xsl:when test="x:hasInnerPagebreaksOrColumnbreaks(.)=true()">
                <xsl:call-template name="footnoteReferenceAndBody">
                    <xsl:with-param name="class" select="'show_in_main'"/>
                    <xsl:with-param name="fullContentMode" select="true()"/>
                </xsl:call-template>
                <xsl:call-template name="footnoteReferenceAndBody">
                    <xsl:with-param name="class" select="'show_in_footnotes'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="footnoteReferenceAndBody">
                    <xsl:with-param name="class" select="'show_in_main_and_footnotes'"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="footnoteReferenceAndBody">
        <xsl:param name="class"/>
        <xsl:param name="fullContentMode"/>
        <xsl:variable name="footnoteReference" select="ancestor::x:footnote[1]/x:footnote.reference" />
        
        <xsl:element name="footnote.reference">
            <xsl:attribute name="class" select="$class"/>
            <xsl:apply-templates select="$footnoteReference" mode="content" />
        </xsl:element>
        <xsl:element name="footnote.body">
            <xsl:attribute name="class" select="$class"/>
            <xsl:choose>
                <xsl:when test="$fullContentMode">
                    <xsl:apply-templates select="node()|@*" mode="fullContent" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="node()|@*" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="x:footnote.reference" />
    
    <xsl:template match="x:footnote.reference" mode="hidden" />
    
    <xsl:template match="x:footnote.reference" mode="content">
        <xsl:apply-templates select="node()|@*" />
    </xsl:template>
    
    <xsl:template match="x:pagebreak | x:columns | x:column | x:endcolumn | x:endcolumns | x:page.number" mode="fullContent" />
    
    <xsl:template match="x:pagebreak">
        <xsl:variable name="outerFootnote" select="ancestor::x:footnote[1]" />

        <xsl:choose>
            <xsl:when test="$outerFootnote">
                <xsl:call-template name="insertCloseTags">
                    <xsl:with-param name="footnote" select="$outerFootnote" />
                </xsl:call-template>
                <xsl:call-template name="processPagebreak" />
                <xsl:call-template name="insertOpenTags">
                    <xsl:with-param name="refId" select="concat($outerFootnote/@id, '-', @num)" />
                    <xsl:with-param name="origId" select="$outerFootnote/@id" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="processPagebreak" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="processPagebreak">
        <xsl:call-template name="addSectionbreak">
            <xsl:with-param name="correspondingNodeInMain" select="$main//x:pagebreak[@num=current()/@num]" />
        </xsl:call-template>
        
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template name="addSectionbreak">
        <xsl:param name="correspondingNodeInMain" />
        
        <xsl:variable name="sectionbreakInMain" select="$correspondingNodeInMain/preceding::x:sectionbreak[1]" />
        <xsl:variable name="noPreceidingXrefs" select="not(x:hasPrecedingXrefsWithFootnotesOnThisPage($sectionbreakInMain, $correspondingNodeInMain, current()))" />
        <xsl:variable name="noPreceidingPagebreaks" select="count($sectionbreakInMain/following::x:pagebreak intersect $correspondingNodeInMain/preceding::x:pagebreak)=0" />

        <xsl:if test="$noPreceidingXrefs and $noPreceidingPagebreaks">
            <xsl:copy-of select="$sectionbreakInMain" />
        </xsl:if>
    </xsl:template>
    
    <xsl:function name="x:hasPrecedingXrefsWithFootnotesOnThisPage">
        <xsl:param name="sectionbreakInMain" as="node()" />
        <xsl:param name="correspondingNodeInMain" as="node()" />
        <xsl:param name="currentNode" as="node()" />
        
        <xsl:variable name="xrefs" select="$sectionbreakInMain/following::x:xref[@type='footnote'] intersect $correspondingNodeInMain/preceding::x:xref[@type='footnote']" />
        <xsl:variable name="footnotes" select="$currentNode/preceding::x:pagebreak[1]/following::x:footnote intersect 
            ($currentNode/preceding::x:footnote | $currentNode/ancestor::x:footnote)" />
        
        <xsl:for-each select="$xrefs">
            <xsl:if test="$footnotes[@id = current()/@origId]">
                <xsl:value-of select="true()" />
            </xsl:if>
        </xsl:for-each>
    </xsl:function>

    <xsl:template name="insertCloseTags">
        <xsl:param name="footnote" />
        
        <xsl:for-each select="ancestor::node()">
            <xsl:sort select="position()" data-type="number" order="descending" />
            <xsl:if test="local-name(.) = 'footnote' or local-name(.) != '' and ancestor::x:footnote">
                <xsl:text disable-output-escaping="yes"><![CDATA[</]]></xsl:text>
                <xsl:value-of select="local-name(.)" />
                <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="insertOpenTags">
        <xsl:param name="refId" />
        <xsl:param name="origId" />
        
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
                <xsl:if test="$isFootnote">
                    <xsl:text disable-output-escaping="yes"><![CDATA[ id="]]></xsl:text>
                    <xsl:value-of select="$refId" />
                    <xsl:text disable-output-escaping="yes"><![CDATA[" origId="]]></xsl:text>
                    <xsl:value-of select="$origId" />
                    <xsl:text disable-output-escaping="yes"><![CDATA["]]></xsl:text>
                </xsl:if>
                <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
                
                <xsl:if test="$isFootnote">
                    <xsl:element name="hidden.footnote.reference">
                        <xsl:attribute name="refId" select="$refId" />
                    </xsl:element>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:function name="x:hasInnerPagebreaksOrColumnbreaks">
        <xsl:param name="node" as="node()"/>
        <xsl:value-of select="count($node//x:pagebreak | $node//x:column | $node//x:endcolumn) > 0" />
    </xsl:function>
    
    <xsl:template name="addCopyOfFootnoteForTableInScrollMode">
        <xsl:param name="correspondingNodeInMain" />
        
        <xsl:variable name="distinguishBetweenPageAndScrollMode" select="$correspondingNodeInMain/ancestor::x:tbl[@mode='page_mode']" />
        
        <xsl:if test="$distinguishBetweenPageAndScrollMode != ''">
            <xsl:element name="footnote">
                <xsl:attribute name="id" select="concat($correspondingNodeInMain/@origId, '-scrollmode')" />
                <xsl:attribute name="origId" select="@id" />
                <xsl:attribute name="orig" select="false()" />
                <xsl:attribute name="hidden" select="true()" />
                <xsl:apply-templates select="node()|@*[not(name()='id')]" />
            </xsl:element>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>