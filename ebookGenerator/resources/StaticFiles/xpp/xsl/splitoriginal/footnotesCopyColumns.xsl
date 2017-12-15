<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    
    <xsl:template match="x:pagebreak">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
        
        <xsl:if test="$fileType='MAIN'">
            <xsl:variable name="footnotesPagebreak" select="$footnotesDocument//x:pagebreak[@num=current()/@num]" />
            <xsl:variable name="footnotesPagebreakNext" select="$footnotesPagebreak/following::x:pagebreak[1]" />
            
            <xsl:variable name="footnotesHasColumns" select="count($footnotesPagebreak/following::x:columns intersect $footnotesPagebreakNext/preceding::x:columns)!=0" />
            
            <xsl:if test="$footnotesHasColumns">
                <xsl:variable name="columnsTag" select="$footnotesPagebreak/following::x:columns[1]" />
                <xsl:variable name="isFootnotesColumnsSplitBySectionbreak" select="count($columnsTag//x:sectionbreak)!=0" />
                
                <xsl:if test="$isFootnotesColumnsSplitBySectionbreak">
                    <xsl:for-each select="$columnsTag//x:footnote">
                        <xsl:variable name="footnote" select="current()" />
                        
                        <xsl:if test="x:shouldCopyXref($footnote)=true()">
                            <xsl:element name="xref">
                                <xsl:attribute name="id" select="concat($footnote/@id, '-column-copy')" />
                                <xsl:attribute name="origId" select="$footnote/@origId" />
                                <xsl:attribute name="type" select="'footnote'" />
                                <xsl:attribute name="hidden" select="true()" />
                                <xsl:attribute name="orig" select="false()" />
                            </xsl:element>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:if>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="x:columns">
        <xsl:choose>
            <xsl:when test="$fileType='FOOTNOTE'">
                <xsl:variable name="isSplitBySectionbreaks" select="count(.//x:sectionbreak)!=0" />
                
                <xsl:choose>
                    <xsl:when test="$isSplitBySectionbreaks">
                        <xsl:copy>
                            <xsl:apply-templates select="node()|@*" mode="additionalColumnFootnotesNosectionbreaks"/>
                        </xsl:copy>
                        <xsl:apply-templates select="node()|@*" mode="hiddenFoootnotesNoColumns"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy>
                            <xsl:apply-templates select="node()|@*"/>
                        </xsl:copy>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node()|@*"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="x:sectionbreak" mode="additionalColumnFootnotesNosectionbreaks"/>
    
    <xsl:template match="x:footnote" mode="additionalColumnFootnotesNosectionbreaks">
        <xsl:variable name="hasFootnoteWhichShowInFootnote" select="count(./x:footnote.body[@class!='show_in_main' or not(@class)])!=0" />
        <xsl:variable name="isNotHiddenFootnote" select="not(@hidden) or @hidden!=true()" />
        
        <xsl:if test="x:shouldCopyFootnote(.)=true()">
            <xsl:element name="footnote">
                <xsl:attribute name="id" select="concat(@id, '-column-copy')" />
                <xsl:attribute name="referenceOnDifferentPageId" select="true()" />
                <xsl:attribute name="xrefId" select="@id" />
                <xsl:attribute name="xrefDoc" select="preceding::x:sectionbreak[1]/@sectionuuid" />
                <xsl:apply-templates select="node()|@*[not(name()='id')]" mode="#current" />
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="x:footnote.reference[@class='show_in_main'] | 
                         x:footnote.body[@class='show_in_main']" 
                  mode="additionalColumnFootnotesNosectionbreaks" />
    
    <xsl:template match="x:column" mode="hiddenFoootnotesNoColumns">
        <xsl:apply-templates select="node()|@*" mode="hiddenFoootnotesNoColumns"/>
    </xsl:template>
    
    <xsl:template match="x:footnote" mode="hiddenFoootnotesNoColumns">
        <xsl:element name="footnote">
            <xsl:attribute name="hidden" select="true()" />
            <xsl:apply-templates select="current()/*|@*[not(name()='hidden')]" />
        </xsl:element>
    </xsl:template>
    
    <xsl:function name="x:shouldCopyXref">
        <xsl:param name="footnote" />
        
        <xsl:variable name="hasNotHiddenFootnoteReference" select="count($footnote/x:hidden.footnote.reference)=0 and count($footnote/x:footnote.reference)!=0" />
        
        <xsl:value-of select="$hasNotHiddenFootnoteReference and x:shouldCopyFootnote($footnote)=true()"/>
    </xsl:function>
    
    <xsl:function name="x:shouldCopyFootnote">
        <xsl:param name="footnote" />        

        <xsl:variable name="isNotHiddenFootnote" select="not($footnote/@hidden) or $footnote/@hidden!=true()" />
        <xsl:variable name="hasFootnoteWhichShowInFootnote" select="count($footnote/x:footnote.body[@class!='show_in_main' or not(@class)])!=0" />
        
        <xsl:value-of select="$isNotHiddenFootnote and $hasFootnoteWhichShowInFootnote"/>
    </xsl:function>
    
</xsl:stylesheet>