<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
    
    <xsl:variable name="COLUMN_COPY" select="'column-copy'" />
    
    <xsl:function name="x:getXrefsOnGivenPageOfMainFile">
        <xsl:param name="pagebreak" as="node()"/> 
        
        <xsl:variable name="nextPagebreakInMainFile" select="$pagebreak/following::x:pagebreak[1]"/>
        
        <xsl:variable name="nullableXrefs" select="$pagebreak/following::x:xref[@type='footnote'] intersect $nextPagebreakInMainFile/preceding::x:xref[@type='footnote']" />
        
        <xsl:choose>
        	<xsl:when test="not($nullableXrefs) and not($pagebreak/following::x:pagebreak)">
        		<xsl:sequence select="$pagebreak/following::x:xref[@type='footnote']" />
        	</xsl:when>
        	<xsl:otherwise>
        		<xsl:sequence select="$nullableXrefs"/>
        	</xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="x:getFootnotesOnGivenPageOfFootnoteFile">
        <xsl:param name="pagebreak" as="node()"/> 
        
        <xsl:variable name="nextPagebreakInFootnotesFile" select="$pagebreak/following::x:pagebreak[1]" />
        <xsl:variable name="nullableFootnotes" select="$pagebreak/following::x:footnote intersect (
        $nextPagebreakInFootnotesFile/preceding::x:footnote | 
        $nextPagebreakInFootnotesFile/ancestor::x:footnote)" />
        <xsl:choose>
        	<xsl:when test="not($nullableFootnotes) and not($pagebreak/following::x:pagebreak)">
        		<xsl:sequence select="$pagebreak/following::x:footnote" />
        	</xsl:when>
        	<xsl:otherwise>
        		<xsl:sequence select="$nullableFootnotes"/>
        	</xsl:otherwise>
        </xsl:choose>
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
    
    <xsl:template match="x:footnote" mode="hiddenFoootnotesNoColumns">
        <xsl:element name="footnote">
            <xsl:attribute name="hidden" select="true()" />
            <xsl:apply-templates select="current()/*|@*[not(name()='hidden')]" />
        </xsl:element>
    </xsl:template>
    
    <xsl:function name="x:shouldCopyFootnote">
        <xsl:param name="footnote" />        

        <xsl:variable name="isNotHiddenFootnote" select="not($footnote/@hidden) or $footnote/@hidden!=true()" />
        <xsl:variable name="hasFootnoteWhichShowInFootnote" select="count($footnote/x:footnote.body[@class!='show_in_main' or not(@class)])!=0" />
        
        <xsl:value-of select="$isNotHiddenFootnote and $hasFootnoteWhichShowInFootnote"/>
    </xsl:function>
    
    <xsl:function name="x:shouldCopyXref">
        <xsl:param name="footnote" />
        
        <xsl:variable name="hasNotHiddenFootnoteReference" select="count($footnote/x:hidden.footnote.reference)=0 and count($footnote/x:footnote.reference)!=0" />
        
        <xsl:value-of select="$hasNotHiddenFootnoteReference and x:shouldCopyFootnote($footnote)=true()"/>
    </xsl:function>
    
    <xsl:function name="x:shouldCopyColumn">
    	<xsl:param name="pagebreak" as="node()" />
        <xsl:variable name="nextPagebreak" select="$pagebreak/following::x:pagebreak[1]" />
            
        <xsl:variable name="hasColumns">
        	<xsl:choose>
        		<xsl:when test="$nextPagebreak">
        			<xsl:value-of select="count($pagebreak/following::x:columns intersect $nextPagebreak/preceding::x:columns)>0" />
        		</xsl:when>
        		<xsl:otherwise>
        			<xsl:value-of select="count($pagebreak/following::x:columns)>0" />
        		</xsl:otherwise>
        	</xsl:choose>
        </xsl:variable>
            
        <xsl:if test="$hasColumns=true()">
	        <xsl:variable name="columnsTag" select="$pagebreak/following::x:columns[1]" />
            <xsl:value-of select="count($columnsTag//x:sectionbreak)!=0" />
        </xsl:if>
    </xsl:function>
</xsl:stylesheet>