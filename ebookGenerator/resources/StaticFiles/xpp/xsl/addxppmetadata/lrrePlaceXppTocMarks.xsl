<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sdl.com/xpp"
    xmlns:x="http://www.sdl.com/xpp" exclude-result-prefixes="x">
    <xsl:import href="placeXppMarks.xsl" />
    <xsl:import href="../transform-utils.xsl" />

    <xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

    <xsl:param name="materialNumber" />

    <xsl:variable name="root_uuid" select="concat($materialNumber, '.', 'lrre')" />
    <xsl:variable name="pagesAmount" select="count(//x:pagebreak)" />

    <xsl:template match="x:root">
        <xsl:element name="root">
            <xsl:variable name="first_lrre_item" select="concat($materialNumber, '.', 'lrre', '1to', x:get-last-page(1, $pagesAmount))" />
            <xsl:call-template name="placeSectionbreak">
                <xsl:with-param name="sectionuuid" select="$first_lrre_item" />
            </xsl:call-template>

            <xsl:call-template name="placeXppHier">
                <xsl:with-param name="uuid" select="$root_uuid" />
                <xsl:with-param name="name" select="'Table of LRRE'" />
                <xsl:with-param name="parent_uuid" select="$root_uuid" />
                <xsl:with-param name="doc_family_uuid" select="$first_lrre_item" />
            </xsl:call-template>

            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>

    <xsl:template match="x:pagebreak">
    	<xsl:choose>
    		<xsl:when test="$pagesAmount > 20 and (./@serial-num=1 or (./@serial-num  - 1) mod 20 = 0)">
    			<xsl:variable name="startPageNumber" select="@serial-num" />
            	<xsl:variable name="startPageLrreItemName" select="x:get-start-page-item-name(.)" />
            	<xsl:variable name="endPageNumber" select="x:get-last-page(@serial-num, $pagesAmount)" />
            	<xsl:variable name="endPageLrreItemName" select="x:get-last-page-item-name(., $endPageNumber)" />
            
            	<xsl:variable name="currentLrreUuid"
                	select="concat($materialNumber, '.', 'lrre', $startPageNumber, 'to', $endPageNumber)" />
            
            	<xsl:if test="@serial-num != 1">
                	<xsl:call-template name="placeSectionbreak">
                   		<xsl:with-param name="sectionuuid" select="$currentLrreUuid" />
                	</xsl:call-template>
            	</xsl:if>
            	
            	<xsl:copy-of select="." />

            	<xsl:call-template name="placeXppHier">
                	<xsl:with-param name="uuid" select="$currentLrreUuid" />
                	<xsl:with-param name="name" select="concat($startPageLrreItemName, ' - ', $endPageLrreItemName)" />
                	<xsl:with-param name="parent_uuid" select="$root_uuid" />
                	<xsl:with-param name="doc_family_uuid" select="$currentLrreUuid" />
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:otherwise>
    			<xsl:copy-of select="." />
    		</xsl:otherwise>
    	</xsl:choose>        
    </xsl:template>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:function name="x:get-last-page">
        <xsl:param name="currentPage" />
        <xsl:param name="globalPagesAmount" />
        <xsl:choose>
            <xsl:when test="$globalPagesAmount - $currentPage >= 20">
                <xsl:value-of select="$currentPage + 19" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$pagesAmount" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="x:get-start-page-item-name">
        <xsl:param name="currentPage" />
        <xsl:value-of select="$currentPage/following::x:t[@style='main.head'][1]/../x:t/text()" />
    </xsl:function>
    
    <xsl:function name="x:get-last-page-item-name">
        <xsl:param name="currentPage" />
        <xsl:param name="lastPageNumber" />
        
        <xsl:variable name="lastPage" select="$currentPage/following::x:pagebreak[@serial-num=$lastPageNumber][1] | $currentPage[@serial-num=$lastPageNumber]"/>
        
        <xsl:variable name="nextPage" select="$lastPage/following::x:pagebreak[1]" />
        
        <xsl:choose>
            <xsl:when test="not($nextPage)">
                <xsl:value-of select="$lastPage/following::x:t[@style='main.head'][last()]/../x:t/text()" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="($lastPage/following::x:t[@style='main.head'] intersect $nextPage/preceding::x:t[@style='main.head'])[last()]/../x:t/text()" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

</xsl:stylesheet>