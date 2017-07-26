<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns="http://www.sdl.com/xpp" xmlns:x="http://www.sdl.com/xpp" 
                exclude-result-prefixes="x">
    <xsl:import href="placeXppMarks.xsl" />
                      
    <xsl:param name="volumeName" />
    <xsl:param name="indexBreaksDoc" />
    <xsl:param name="indexBreaks" select="document($indexBreaksDoc)" />
                      
	<xsl:variable name="root_uuid" select="concat($volumeName, '.', 'index')" />
	
	<xsl:template match="x:root">
		<root>
			<xsl:variable name="firstIndexChar" select="substring(./x:INDEX/x:l1[1]/x:t[1]/text(), 1, 1)" />
			<xsl:variable name="first_index_item" select="concat($volumeName, '.', 'index', $firstIndexChar, $indexBreaks/x:indexbreaks/x:indexbreak[@startChar = $firstIndexChar]/@endChar)" />
			
			<xsl:call-template name="placeSectionbreak">
				<xsl:with-param name="sectionuuid" select="$first_index_item" />
			</xsl:call-template>
			
			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$root_uuid" />
				<xsl:with-param name="name" select="'Index'" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$first_index_item"/>
			</xsl:call-template>
			
			<xsl:apply-templates />
		</root>	
	</xsl:template>
	
	<xsl:template match="x:l1">
		<xsl:variable name="currentLetter" select="substring(./x:t[1]/text(), 1, 1)" />
		<xsl:variable name="previousLetter" select="substring(preceding::x:l1[1]/x:t[1]/text(), 1, 1)" />
		<xsl:variable name="indexBreak" select="$indexBreaks/x:indexbreaks/x:indexbreak[@startChar = $currentLetter]" />
		
		<xsl:if test="$currentLetter != $previousLetter and $indexBreak">
			<xsl:variable name="currentIndexUuid" select="concat($volumeName, '.', 'index', $currentLetter, $indexBreak/@endChar)" />
		
			<xsl:if test="$previousLetter != ''">
				<xsl:call-template name="placeSectionbreak">
					<xsl:with-param name="sectionuuid" select="$currentIndexUuid" />
				</xsl:call-template>
			</xsl:if>

			<xsl:call-template name="placeXppHier">
				<xsl:with-param name="uuid" select="$currentIndexUuid" />
				<xsl:with-param name="name" select="concat($currentLetter, '-', $indexBreak/@endChar)" />
				<xsl:with-param name="parent_uuid" select="$root_uuid" />
				<xsl:with-param name="doc_family_uuid" select="$currentIndexUuid"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:copy-of select="." />
    </xsl:template>
</xsl:stylesheet>